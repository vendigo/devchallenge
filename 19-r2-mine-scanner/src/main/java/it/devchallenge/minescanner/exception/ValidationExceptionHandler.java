package it.devchallenge.minescanner.exception;

import it.devchallenge.minescanner.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    protected ResponseEntity<ErrorResponse> handleValidationException(InvalidInputException ex) {
        return ResponseEntity.unprocessableEntity()
                .body(new ErrorResponse(ex.getMessage()));
    }
}
