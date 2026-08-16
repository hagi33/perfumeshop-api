package com.fabio.perfumeshop_api.order.internal.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Evento de dominio: un pedido ha sido pagado.
 * Record inmutable: un evento es un hecho del pasado, no se modifica.
 * Self-contained: lleva los datos ya resueltos que el email necesita,
 * para que el listener (AFTER_COMMIT) no toque la BD ni colecciones lazy.
 */
public record OrderPaidEvent(
        Long orderId,
        String recipientEmail,
        Instant createdAt,
        BigDecimal total,
        List<Item> items
) {
    public record Item(String perfumeName, int quantity, BigDecimal unitPrice) {}
}
