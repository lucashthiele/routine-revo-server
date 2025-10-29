package com.lucashthiele.routine_revo_server.infrastructure.web.auth;

import com.lucashthiele.routine_revo_server.usecase.auth.exception.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ControllerAdvice
public class AuthExceptionMapper {
  @ExceptionHandler(InvalidCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException invalidCredentialsException) {
    Map<String, String> errorBody = Map.of("error", invalidCredentialsException.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
  }
}
