package com.fabio.perfumeshop_api.order.internal.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(

        List<CartItemResponse> items,
        BigDecimal total //suma de todos los subtotales
) {
}
