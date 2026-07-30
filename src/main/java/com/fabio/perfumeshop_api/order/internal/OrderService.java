package com.fabio.perfumeshop_api.order.internal;


import com.fabio.perfumeshop_api.catalog.api.CatalogApi;
import com.fabio.perfumeshop_api.catalog.api.CatalogItem;
import com.fabio.perfumeshop_api.user.api.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CatalogApi catalogApi;
    private final UserApi userApi;
    private final OrderMapper orderMapper;

    private Long getCurrentUserId(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userApi.findIdByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

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



}
