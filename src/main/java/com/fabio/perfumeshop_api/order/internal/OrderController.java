package com.fabio.perfumeshop_api.order.internal;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
