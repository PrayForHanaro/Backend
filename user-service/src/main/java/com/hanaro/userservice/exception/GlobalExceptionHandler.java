package com.hanaro.userservice.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegal(IllegalArgumentException e) {
    return ResponseEntity
        .badRequest()
        .body(e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleAll(Exception e) {
    return ResponseEntity
        .internalServerError()
        .body("Server error: " + e.getMessage());
  }
}
