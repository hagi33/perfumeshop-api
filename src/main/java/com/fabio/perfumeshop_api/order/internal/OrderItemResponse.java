package com.fabio.perfumeshop_api.order.internal;

import java.math.BigDecimal;

public record OrderItemResponse(

        Long perfumeId,
        String perfumeName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal subtotal


) {
}
