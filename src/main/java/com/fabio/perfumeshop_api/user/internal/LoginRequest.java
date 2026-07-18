package com.fabio.perfumeshop_api.user.internal;

import jakarta.validation.constraints.NotBlank;

record LoginRequest(
        @NotBlank String email,
        @NotBlank String password

) {

}
