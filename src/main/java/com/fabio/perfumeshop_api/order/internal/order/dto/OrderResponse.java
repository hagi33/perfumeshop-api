package com.fabio.perfumeshop_api.order.internal.order.dto;

import com.fabio.perfumeshop_api.order.internal.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(

        Long orderId,
        Instant createdAt,
        OrderStatus status,
        BigDecimal total,
        List<OrderItemResponse> items

        ) {
}
