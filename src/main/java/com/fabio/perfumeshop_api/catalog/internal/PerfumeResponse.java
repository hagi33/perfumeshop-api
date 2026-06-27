package com.fabio.perfumeshop_api.catalog.internal;

import java.math.BigDecimal;

public record PerfumeResponse(
        Long id, String name, String brand,
        String description, OlfactoryFamily family,
        Cocentration cocentration, Gender gender,
        Integer volumeM, BigDecimal price, int stock,
        String imageUrl) {
}
