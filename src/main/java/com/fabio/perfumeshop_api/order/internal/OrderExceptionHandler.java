package com.fabio.perfumeshop_api.order.internal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejo de errores propio del módulo order (carrito).
 *
 * Cada módulo traduce sus excepciones a ProblemDetail (RFC 7807), igual que
 * CatalogExceptionHandler y UserExceptionHandler. Aunque comparte el nombre
 * "ResourceNotFoundException" con catalog, son clases distintas (paquetes
 * distintos): Spring resuelve el handler por el tipo exacto, sin ambigüedad.
 */
@RestControllerAdvice
class OrderExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso no encontrado");
        return problem;
    }
}
