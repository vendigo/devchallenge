package it.devchallenge.hashphone.validation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ErrorHandler {

    @ExceptionHandler({ IllegalArgumentException.class, ValidationException.class, })
    protected ResponseEntity<Object> handleValidationException(RuntimeException ex) {
        return ResponseEntity.badRequest()
            .body(ex.getMessage());
    }
}
