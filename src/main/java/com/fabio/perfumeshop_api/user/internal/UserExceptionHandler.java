package com.fabio.perfumeshop_api.user.internal;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class UserExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    ProblemDetail handleinvalidCredentials(InvalidCredentialsException ex){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, ex.getMessage()
        );
        problem.setTitle("Credenciales inválidas");
        return problem;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    ProblemDetail handleEmailExists(EmailAlreadyExistsException ex){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage()
        );
        problem.setTitle("Email ya registrado");
        return problem;

    }




}
