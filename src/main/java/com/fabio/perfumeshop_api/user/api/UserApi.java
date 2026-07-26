package com.fabio.perfumeshop_api.user.api;

import java.util.Optional;

/**
 * Punto de entrada del módulo user para el resto de la aplicación.
 * Interfaz mínima: solo lo que otros módulos necesitan de verdad.
 */
public interface UserApi {

    /**
     * Devuelve el id del usuario a partir de su email, o vacío si no existe.
     * Otros módulos identifican al usuario autenticado por su email (que viene
     * en el token) y necesitan el id para sus propias referencias.
     */
    Optional<Long> findIdByEmail(String email);
}
