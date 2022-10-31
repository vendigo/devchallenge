package it.devchallenge.trustnetwork.controller;

import it.devchallenge.trustnetwork.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<String> handleValidationException(ValidationException ex) {
        return ResponseEntity.badRequest()
                .body(ex.getMessage());
    }
}
