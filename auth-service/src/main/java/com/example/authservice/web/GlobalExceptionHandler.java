package com.example.authservice.web;

import com.example.authservice.web.dto.Responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
      .map(f -> f.getField() + " " + f.getDefaultMessage()).collect(Collectors.joining("; "));
    return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", msg));
  }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
  }
}
