package com.lucashthiele.routine_revo_server.infrastructure.web.onboarding;

import com.lucashthiele.routine_revo_server.usecase.onboarding.exception.InvalidOnboardingTokenException;
import com.lucashthiele.routine_revo_server.usecase.onboarding.exception.UserNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice()
public class OnboardingExceptionMapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingExceptionMapper.class);
  
  @ExceptionHandler(InvalidOnboardingTokenException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<Map<String, String>> handleInvalidResetToken(InvalidOnboardingTokenException invalidOnboardingTokenException) {
    LOGGER.error("Invalid onboarding token error: {}", invalidOnboardingTokenException.getMessage());
    Map<String, String> errorBody = Map.of("error", invalidOnboardingTokenException.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorBody);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Map<String, String>> handleInvalidData(MethodArgumentNotValidException methodArgumentNotValidException) {
    LOGGER.error("Validation error in onboarding request: {}", methodArgumentNotValidException.getMessage());
    Map<String, String> errors = new HashMap<>();
    methodArgumentNotValidException.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException userNotFoundException) {
    LOGGER.error("User with status PENDING not found error: {}", userNotFoundException.getMessage());
    Map<String, String> errorBody = Map.of("error", userNotFoundException.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorBody);
  }
}
