package com.fabio.perfumeshop_api.order.internal.exception;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException() {
        super("No se hacer el pedido, el carrito está vacío");
    }
}
