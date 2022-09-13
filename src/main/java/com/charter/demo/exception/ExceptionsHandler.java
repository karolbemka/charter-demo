package com.charter.demo.exception;

import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(DemoException.class)
    public ResponseEntity<DemoErrorResponse> handleDemoException(final DemoException exception) {
        log.warn("handleDemoException: errorMessage={}, exception", exception.getMessage(), exception);
        final DemoErrorResponse demoErrorResponse = new DemoErrorResponse(exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus()).body(demoErrorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DemoErrorResponse> handleRuntimeException(final RuntimeException exception) {
        log.error("Unexpected exception has been thrown: errorMessage={}, exception", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DemoErrorResponse(DemoException.UNKNOWN_ERROR_MESSAGE));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DemoErrorResponse> handleMethodArgumentNotValidException (final MethodArgumentNotValidException exception) {
        log.warn("handleMethodArgumentNotValidException: errorMessage{}, exception", exception.getMessage(), exception);

        final String mappedErrorMessages = mapFieldErrorsToErrorMessage(exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DemoErrorResponse(mappedErrorMessages));
    }

    private String mapFieldErrorsToErrorMessage(final MethodArgumentNotValidException exception) {
        return exception.getAllErrors().stream()
            .map(error -> {
                final FieldError fieldError = (FieldError) error;
                return String.format("field {%s} has failed validation due to {%s}", fieldError.getField(), fieldError.getDefaultMessage());
            })
            .collect(Collectors.toList()).toString();
    }
}
