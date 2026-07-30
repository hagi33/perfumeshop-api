package com.fabio.perfumeshop_api.catalog.internal;

class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
