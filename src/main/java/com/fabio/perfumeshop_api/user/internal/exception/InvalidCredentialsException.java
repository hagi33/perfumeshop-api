package com.fabio.perfumeshop_api.user.internal.exception;

public class InvalidCredentialsException extends RuntimeException {
     public InvalidCredentialsException()
     {
        super("Email o contraseña incorrectos");
    }
}
