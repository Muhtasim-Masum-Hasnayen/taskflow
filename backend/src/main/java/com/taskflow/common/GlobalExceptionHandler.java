package com.taskflow.common;
import java.time.Instant;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
  public record FieldErr(String field, String message) {}
  public record ApiError(Instant timestamp, int status, String code, String message, List<FieldErr> fieldErrors) {}
  @ExceptionHandler(ApiException.class)
  ResponseEntity<ApiError> api(ApiException e) {
    return ResponseEntity.status(e.status()).body(new ApiError(Instant.now(), e.status().value(), e.code(), e.getMessage(), List.of()));
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> invalid(MethodArgumentNotValidException e) {
    var fields = e.getBindingResult().getFieldErrors().stream().map(f -> new FieldErr(f.getField(), f.getDefaultMessage())).toList();
    return ResponseEntity.badRequest().body(new ApiError(Instant.now(), 400, "VALIDATION_ERROR", "Invalid request", fields));
  }
}
