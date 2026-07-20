package com.fabio.perfumeshop_api.user.internal;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Ya existe un usuario con el mismo email" + email);
    }
}
