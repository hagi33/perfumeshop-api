package com.fabio.perfumeshop_api.order.internal.cart;

import com.fabio.perfumeshop_api.catalog.api.CatalogApi;
import com.fabio.perfumeshop_api.order.internal.cart.dto.CartResponse;
import com.fabio.perfumeshop_api.order.internal.exception.ResourceNotFoundException;
import com.fabio.perfumeshop_api.user.api.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class CartService {

    private final CartRepository cartRepository;
    private final CatalogApi catalogApi; // para validar perfumes y consultar precio/stock
    private final UserApi userApi; // para obtener el id del usuario autenticado
    private final CartMapper cartMapper;


    //Método que resuelve quien hace la petición
    private Long getCurrentUserId(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userApi.findIdByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

    }


    private Cart getOrCreateCart(Long userId){
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().userId(userId).build()));




    }

    @Transactional
    CartResponse getCart(){
    Long userId = getCurrentUserId();

    Cart cart = getOrCreateCart(userId);
    return cartMapper.toResponse(cart);


    }

    @Transactional
    CartResponse updateItem(Long perfumeId, int newQuantity){
    Long userId = getCurrentUserId();
    Cart cart = getOrCreateCart(userId);

    cart.getItems().stream()
            .filter(item -> item.getPerfumeId().equals(perfumeId))
            .findFirst()
            .ifPresentOrElse(item -> item.setQuantity(newQuantity),
                    () -> {throw  new ResourceNotFoundException(
                            "Perfume no encontrado con id: " + perfumeId
                    );}
            );
            Cart saved = cartRepository.save(cart);
            return cartMapper.toResponse(saved);


    }

    @Transactional
    CartResponse removeItem(Long perfumeId){
        Long userId = getCurrentUserId();
        Cart cart = getOrCreateCart(userId);

        boolean removed = cart.getItems().removeIf(item -> item.getPerfumeId().equals(perfumeId));

        if (!removed){
                throw new ResourceNotFoundException("Perfume no encontrado con id: " + perfumeId);
        }
        Cart saved = cartRepository.save(cart);
        return cartMapper.toResponse(saved);

    }

    @Transactional
    void clearCart(){
        Long userId = getCurrentUserId();
        Cart cart = getOrCreateCart(userId);

        cart.getItems().clear();

        cartRepository.save(cart);
    }







    @Transactional
    CartResponse addItem(Long perfumeId, int quantity){
        Long userId= getCurrentUserId();

        // 1. Validar que el perfume existe (vía CatalogApi, sin tocar catalog por dentro).
        catalogApi.findById(perfumeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfume no encontrado con id: " + perfumeId
                ));

        Cart cart = getOrCreateCart(userId);

        // 2. ¿Ya está ese perfume en el carrito? Si sí, sumar; si no, añadir línea.
        cart.getItems().stream()
                .filter(item -> item.getPerfumeId().equals(perfumeId))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + quantity),
                        //Lo que pasa si no se encuentra el item
                        () -> cart.getItems().add(CartItem.builder()
                                .cart(cart)
                                .perfumeId(perfumeId)
                                .quantity(quantity)
                                .build())
                        );

        Cart saved = cartRepository.save(cart);
        return cartMapper.toResponse(saved);

    }





}
