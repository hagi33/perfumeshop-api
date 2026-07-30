package com.fabio.perfumeshop_api.order.internal;


import com.fabio.perfumeshop_api.catalog.api.CatalogApi;
import com.fabio.perfumeshop_api.catalog.api.CatalogItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
class CartMapper {


    private final CatalogApi catalogApi;

    CartResponse toResponse(Cart cart){

        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(this::toItemResponse)
                .toList();

        //El total es la suma de todos los subtotales
        BigDecimal total = items.stream()
                .map(CartItemResponse::subTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(items, total);
    }


    //Posible método a cambiar en el futuro por posible falla en la escalabilidad (problema N + 1)
    private CartItemResponse toItemResponse(CartItem item){

        //Consultamos catalog para obtener el nombre y precio actual del perfume
        CatalogItem perfume = catalogApi.findById(item.getPerfumeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfume no encontrado con id: " + item.getPerfumeId()
                ));

        BigDecimal subtotal = perfume.price()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                item.getPerfumeId(),
                perfume.name(),
                perfume.price(),
                item.getQuantity(),
                subtotal
        );


    }





}
