package com.fabio.perfumeshop_api.order.internal;

import java.math.BigDecimal;

public record CartItemResponse(

        Long perfumeId,
        String perfumeName,
        BigDecimal unitPrice,
        int quantity,        // precio actual del perfume
        BigDecimal subTotal //unitPrice * quantity


        ){}
