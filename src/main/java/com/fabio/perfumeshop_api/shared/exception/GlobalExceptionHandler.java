package com.fabio.perfumeshop_api.shared.exception;

import com.fabio.perfumeshop_api.catalog.api.InsufficientStockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice//Esta anotación hace que la clase capte todas las excepciones producidas por las clases controller del monolito
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Hay errores de validación");
        problem.setTitle("Petición no válida");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e ->
                errors.put(e.getField(), e.getDefaultMessage()));
        problem.setProperty("errors", errors);
        return problem;
    }



        //Método que controla excepeciones no esperadas
        @ExceptionHandler(Exception.class)
        ProblemDetail handleGeneric(Exception ex){
        log.error("Error no controlado en la API", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
          HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error inesperado"
        );
        problem.setTitle("Error interno");
        return problem;
        }




}
