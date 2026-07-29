package com.fabio.perfumeshop_api.order.internal;

/**
 * Se lanza cuando el módulo order no encuentra un recurso que se esperaba
 * (un perfume que no existe en el catálogo, o una línea que no está en el carrito).
 *
 * Es propia del módulo order: así no dependemos de la excepción interna de catalog
 * y respetamos la frontera modular (cada módulo maneja sus propias excepciones,
 * igual que catalog y user).
 */
class ResourceNotFoundException extends RuntimeException {
    ResourceNotFoundException(String message) {
        super(message);
    }
}
