package com.fabio.perfumeshop_api.order.internal.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddItemRequest(

        @NotNull(message = "El id del perfume es obligatorio")
        Long perfumeId,

        @NotNull
        @Positive(message = "La cantidad debe ser mayor que 0")
        Integer quantity
) {
}
