package com.fabio.perfumeshop_api.catalog.api;



/**
 * Evento de dominio: stock de un perfume ha disminuido.
 * Record inmutable: un evento es un hecho del pasado, no se modifica.
 * */
public record StockDecreasedEvent(
        Long perfumeId,
        int quantityRemoved,
        int remainingStock

) {
}
