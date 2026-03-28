package com.example.loanriskevaluationsystem.shared.exception;

import com.example.loanriskevaluationsystem.shared.dto.ApidtoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import java.util.stream.Collectors;
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApidtoResponse<Void>> handleBaseException(BaseException ex) {
        log.error("Exception: {}", ex.getMessage());

        ApidtoResponse<Void> response = ApidtoResponse.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApidtoResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("Validation error: {}", errorMessage);

        ApidtoResponse<Void> response = ApidtoResponse.<Void>builder()
                .success(false)
                .message("Validation error: " + errorMessage)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApidtoResponse<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch error: {}", ex.getMessage());

        ApidtoResponse<Void> response = ApidtoResponse.<Void>builder()
                .success(false)
                .message("Invalid parameter type for: " + ex.getName())
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApidtoResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ApidtoResponse<Void> response = ApidtoResponse.<Void>builder()
                .success(false)
                .message("Internal server error")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.internalServerError().body(response);
    }
}