package com.fabio.perfumeshop_api.catalog.internal;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreatePerfumeRequest(
        @NotBlank String name,
        @NotBlank String brand,
        @Size(max = 1000) String description,
        @NotNull OlfactoryFamily family,
        @NotNull Cocentration concentration,
        @NotNull Gender gender,
        @NotNull @Positive Integer volumeMl,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        @NotNull @PositiveOrZero Integer stock,
        String imageUrl

        ) {

}
