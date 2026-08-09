package com.example.analyticssvc.web.exception;

import com.example.analyticssvc.exception.AnalyticsException;
import com.example.analyticssvc.web.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AnalyticsException.class)
    public ResponseEntity<ErrorResponseDto> handleAnalyticsException(
            HttpServletRequest request, AnalyticsException ex) {
        log.error("AnalyticsException [{}]: {}", ex.getHttpStatus(), ex.getMessage());

        ErrorResponseDto body = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    // Built-in — невалиден UUID в path variable
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(
            HttpServletRequest request, MethodArgumentTypeMismatchException ex) {
        log.error("Type mismatch: {}", ex.getMessage());

        ErrorResponseDto body = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Invalid parameter: " + ex.getName())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(
            HttpServletRequest request, Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponseDto body = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
