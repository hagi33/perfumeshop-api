package com.fabio.perfumeshop_api.catalog.internal;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
