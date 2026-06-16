package com.fitnesscenter.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setMessage("Ошибка валидации");
        response.setErrors(errors);
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NegativeStockException.class)
    public ResponseEntity<ErrorResponse> handleNegativeStock(NegativeStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(ZoneHasEquipmentException.class)
    public ResponseEntity<ErrorResponse> handleZoneHasEquipment(ZoneHasEquipmentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }

    public static class ValidationErrorResponse {
        private String message;
        private Map<String, String> errors;
        private LocalDateTime timestamp;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Map<String, String> getErrors() { return errors; }
        public void setErrors(Map<String, String> errors) { this.errors = errors; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    public static class ErrorResponse {
        public String message;
        public LocalDateTime timestamp;

        public ErrorResponse(String message, LocalDateTime timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}