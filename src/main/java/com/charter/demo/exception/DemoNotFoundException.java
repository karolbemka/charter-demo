package com.charter.demo.exception;

import org.springframework.http.HttpStatus;

public class DemoNotFoundException extends DemoException {
    private final HttpStatus httpStatus;

    public DemoNotFoundException(final String message) {
        super(message);
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
