package com.example.analyticssvc.exception;

import org.springframework.http.HttpStatus;

public class AchievementNotFoundException extends AnalyticsException {
    public AchievementNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
