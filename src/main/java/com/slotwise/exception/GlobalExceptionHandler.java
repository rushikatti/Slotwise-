package com.slotwise.exception;

import com.slotwise.dto.ApiResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

        @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", null);
        body.put("error", "You do not have permission to perform this action");
        body.put("success", false);
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

        @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", null);
        body.put("error", ex.getMessage());
        body.put("success", false);
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

        @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", null);
        body.put("error", ex.getMessage());
        body.put("success", false);
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
    
        @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", null);
        body.put("error", message);
        body.put("success", false);
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


        // last line of defence: the database itself refused a duplicate
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", null);
        body.put("error", "This request conflicts with existing data");
        body.put("success", false);
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // two people changing the same slot at once: your Slot entity's @Version catches it
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLock(OptimisticLockingFailureException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("data", null);
        body.put("error", "This slot was just taken by someone else. Please pick another slot.");
        body.put("success", false);
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

        @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Something went wrong: " + ex.getMessage()));
    }
}