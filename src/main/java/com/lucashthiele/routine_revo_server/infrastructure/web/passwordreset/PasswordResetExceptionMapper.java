package com.lucashthiele.routine_revo_server.infrastructure.web.passwordreset;

import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class PasswordResetExceptionMapper {
  
  @ExceptionHandler(InvalidResetTokenException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<Map<String, String>> handleInvalidResetToken(InvalidResetTokenException invalidResetTokenException) {
    Map<String, String> errorBody = Map.of("error", invalidResetTokenException.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
  }
  
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Map<String, String>> handleInvalidData(MethodArgumentNotValidException methodArgumentNotValidException) {
    Map<String, String> errors = new HashMap<>();
    methodArgumentNotValidException.getBindingResult().getFieldErrors().forEach(error -> 
        errors.put(error.getField(), error.getDefaultMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }
}
