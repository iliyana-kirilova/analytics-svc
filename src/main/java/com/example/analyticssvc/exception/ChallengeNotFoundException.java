package com.example.analyticssvc.exception;

import org.springframework.http.HttpStatus;

public class ChallengeNotFoundException extends AnalyticsException {
    public ChallengeNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
