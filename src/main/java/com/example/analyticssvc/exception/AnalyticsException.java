package com.example.analyticssvc.exception;

import org.springframework.http.HttpStatus;

public abstract class AnalyticsException extends RuntimeException {
    public AnalyticsException(String message) {
        super(message);
    }

    public abstract HttpStatus getHttpStatus();

}
