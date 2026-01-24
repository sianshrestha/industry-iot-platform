package com.industrial.iot_platform.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 1. Handle "Device already exists" (409 Conflict)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleArgumentException(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 2. Handle "Invalid credentials" (401 Unauthorized)
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // 3. Handle Validation Errors (e.g. missing fields)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorResponse.put(error.getField(), error.getDefaultMessage())
        );
        errorResponse.put("status", "400");
        errorResponse.put("error", "Validation Failed");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 4. Catch-all for anything else (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
    }

    // Helper method to format the JSON response
    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        response.put("status", String.valueOf(status.value()));
        // timestamp is added automatically by Spring, but we can add custom fields here
        return new ResponseEntity<>(response, status);
    }
}
