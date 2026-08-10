package com.fabio.perfumeshop_api.order.internal.order;


import com.fabio.perfumeshop_api.order.internal.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
class OrderController {

    private final OrderService orderService;


    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    OrderResponse checkout(){
        return orderService.checkout();
    }


    @PostMapping("/{orderId}/pay")
    OrderResponse pay(@PathVariable Long orderId){
        return orderService.pay(orderId);
    }


    @GetMapping
    List<OrderResponse> getUserOrder(){
        return orderService.getUserOrders();
    }

}
