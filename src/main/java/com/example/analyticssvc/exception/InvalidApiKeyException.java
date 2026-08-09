package com.example.analyticssvc.exception;

import org.springframework.http.HttpStatus;

public class InvalidApiKeyException extends AnalyticsException {
    private final HttpStatus httpStatus;

    public InvalidApiKeyException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
