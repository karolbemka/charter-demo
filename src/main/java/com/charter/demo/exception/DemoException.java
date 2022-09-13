package com.charter.demo.exception;

import org.springframework.http.HttpStatus;

public class DemoException extends RuntimeException {
    public static final String UNKNOWN_ERROR_MESSAGE = "Internal Error";

    protected final HttpStatus httpStatus;

    public DemoException(final String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
