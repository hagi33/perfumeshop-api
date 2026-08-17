package com.fabio.perfumeshop_api.order.internal.order;

import com.fabio.perfumeshop_api.catalog.api.CatalogApi;
import com.fabio.perfumeshop_api.catalog.api.CatalogItem;
import com.fabio.perfumeshop_api.order.internal.cart.Cart;
import com.fabio.perfumeshop_api.order.internal.cart.CartItem;
import com.fabio.perfumeshop_api.order.internal.cart.CartRepository;
import com.fabio.perfumeshop_api.order.internal.exception.EmptyCartException;
import com.fabio.perfumeshop_api.order.internal.exception.InvalidOrderStateException;
import com.fabio.perfumeshop_api.order.internal.exception.ResourceNotFoundException;
import com.fabio.perfumeshop_api.order.internal.order.dto.OrderPaidEvent;
import com.fabio.perfumeshop_api.order.internal.order.dto.OrderResponse;
import com.fabio.perfumeshop_api.user.api.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CatalogApi catalogApi;
    private final UserApi userApi;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    private Long getCurrentUserId(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userApi.findIdByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

    }

    //Método para conseguir el email, que se usará para enviar emails una vez pagado el pedido.
    private String getCurrentUserEmail(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }






    @Transactional
    OrderResponse checkout(){
        //primero sacamos el usuario y el carrito del usuario.
        Long userId = getCurrentUserId();
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
                () -> new EmptyCartException() //Si no hay carrito es que está vacío
        );

        //Comprobamos si el carrito está vacío.
        if (cart.getItems().isEmpty()){
            throw new EmptyCartException();
        }

        //Creamos el pedido sin productos todavía, status: PENDING
        Order order = Order.builder()
                .userId(userId)
                .createdAt(Instant.now())
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        //Iteramos por cada línea/item del pedido
        for (CartItem cartItem : cart.getItems()){
            CatalogItem perfume = catalogApi.findById(cartItem.getPerfumeId()).orElseThrow(
                    () -> new ResourceNotFoundException("No se ha encontrado el perfume " + cartItem.getPerfumeId())
            );

            catalogApi.decreaseStock(cartItem.getPerfumeId(),  cartItem.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .perfumeId(perfume.id())
                    .perfumeName(perfume.name())
                    .unitPrice(perfume.price())
                    .quantity(cartItem.getQuantity())
                    .build();
            order.getItems().add(orderItem);

            //Acumulamos el total del orderItem (multiplicamos el precio del perfume por las unidades que se pidan)
            total = total.add(
                    perfume.price().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        //Fijamos el total
        order.setTotal(total);

        //Guardamos el pedido
        Order saved = orderRepository.save(order);

        //vaciamos el carrito
        cart.getItems().clear();
        cartRepository.save(cart);

        //devolvemos el pedido mapeado.
        return orderMapper.toResponse(saved);


    }


    @Transactional
    OrderResponse pay(Long orderId){

        Long userId = getCurrentUserId();

        //Buscamos el pedido y lanzamos una excepción si no existe
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido no encontrado: " + orderId)
        );


        //Comprobamos que el pedido es el del usuario
        if (!order.getUserId().equals(userId)){
            throw new ResourceNotFoundException("Pedido no encontrado: " + orderId);
        }

        //Comprobamos el estado del  order
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("El pedido no está pendiente de pago, " +
                    "extado actual : " + order.getStatus());
        }

        //Si ha pasado los filtros cambiamos el estado a paid
        order.setStatus(OrderStatus.PAID);
        Order saved = orderRepository.save(order);

        //Construimos la foto inmutable del pedido para el evento, dentro de la transacción, donde la coleccion lazy items sí se puede leer.
        List<OrderPaidEvent.Item> eventItems = saved.getItems().stream()
                .map(item -> new OrderPaidEvent.Item(
                        item.getPerfumeName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                )).toList();


        OrderPaidEvent event = new OrderPaidEvent(
                saved.getId(),
                getCurrentUserEmail(),
                saved.getCreatedAt(),
                saved.getTotal(),
                eventItems
        );

        applicationEventPublisher.publishEvent(event);

        return orderMapper.toResponse(saved);

    }

    List<OrderResponse> getUserOrders(){
        Long userId = getCurrentUserId();

        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }







}
