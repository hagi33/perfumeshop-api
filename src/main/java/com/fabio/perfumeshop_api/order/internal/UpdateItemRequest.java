package com.fabio.perfumeshop_api.order.internal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateItemRequest(
        @NotNull
        @Positive(message = "La cantidad debe ser mayor que 0")
        Integer quantity
) {
}
