package com.fabio.perfumeshop_api.catalog.api;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long perfumeId, int quantityRequested, int avalaible) {
        super("Insufficient stock for the perfume: " + perfumeId
        + ": are requested " + quantityRequested + " and there are " + avalaible + "avalaible");
    }
}
