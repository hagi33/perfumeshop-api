package com.fabio.perfumeshop_api.order.internal.order;


import com.fabio.perfumeshop_api.order.internal.order.dto.OrderItemResponse;
import com.fabio.perfumeshop_api.order.internal.order.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
class OrderMapper {

    OrderResponse toResponse(Order order){
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

            return new OrderResponse(
                    order.getId(),
                    order.getCreatedAt(),
                    order.getStatus(),
                    order.getTotal(),
                    items
            );
    }


    private OrderItemResponse toItemResponse(OrderItem item){
        BigDecimal subtotal = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new OrderItemResponse(

                item.getPerfumeId(),
                item.getPerfumeName(),
                item.getUnitPrice(),
                item.getQuantity(),
                subtotal
        );

    }

}
