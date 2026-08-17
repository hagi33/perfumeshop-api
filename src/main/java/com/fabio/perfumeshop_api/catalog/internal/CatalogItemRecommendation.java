package com.fabio.perfumeshop_api.catalog.internal;

import java.util.List;

/**
 * Vista pública de un perfume pensada para recomendación (chat).
 * Incluye familia olfativa y notas, que findById/CatalogItem no exponen
 * porque order no las necesitaba. No expone la entidad JPA.
 */
public record CatalogItemRecommendation(
        Long id,
        String name,
        String brand,
        String family,
        List<String> notes

) {
}
