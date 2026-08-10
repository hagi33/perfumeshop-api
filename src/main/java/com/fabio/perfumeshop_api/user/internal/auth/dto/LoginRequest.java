package com.fabio.perfumeshop_api.user.internal.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password

) {

}
