package com.fabio.perfumeshop_api.catalog.internal;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

record CreatePerfumeRequest(
        @NotBlank String name,
        @NotBlank String brand,
        @Size(max = 250) String description,
        @NotNull OlfactoryFamily family,
        @NotNull Concentration concentration,
        @NotNull Gender gender,
        @NotNull @Positive Integer volumeMl,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @NotNull @PositiveOrZero Integer stock,
        String imageUrl

        ) {

}
