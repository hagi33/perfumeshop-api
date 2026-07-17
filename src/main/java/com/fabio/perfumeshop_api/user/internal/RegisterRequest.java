package com.fabio.perfumeshop_api.user.internal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        @Email(message = "El email tiene que tener un formato válido.")
        String email,

        @NotBlank
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caractéres.")
        String password
) {
}
