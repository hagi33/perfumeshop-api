package com.fabio.perfumeshop_api.user.internal;

public class InvalidCredentialsException extends RuntimeException {
     InvalidCredentialsException()
     {
        super("Email o contraseña incorrectos");
    }
}
