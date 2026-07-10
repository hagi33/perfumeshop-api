package com.fabio.perfumeshop_api.shared.exception;

import com.fabio.perfumeshop_api.catalog.api.InsufficientStockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice//Esta anotación hace que la clase capte todas las excepciones producidad
public class GlobalExceptionHandler {


        @ExceptionHandler(ResourceNotFoundException.class)
        ProblemDetail handleNotFound(ResourceNotFoundException ex){
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND, ex.getMessage());
            problem.setTitle("Recurso no encontrado");
            return problem;
        }

        @ExceptionHandler(InsufficientStockException.class)
        ProblemDetail handleInsufficienteStoc(InsufficientStockException ex){
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT, ex.getMessage());
            problem.setTitle("Stock insuficiente");
            return problem;
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        ProblemDetail handleValidation(MethodArgumentNotValidException ex){
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST, "Hay errores de validación");
            problem.setTitle("Petición no válida");
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(e ->
                    errors.put(e.getField(), e.getDefaultMessage()));
            problem.setProperty("errors", errors);
            return problem;
        }




}
