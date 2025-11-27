package com.lucashthiele.routine_revo_server.infrastructure.web.user;

import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import com.lucashthiele.routine_revo_server.usecase.user.exception.JsonProcessingRuntimeException;
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

@ControllerAdvice
public class UserControllerExceptionMapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerExceptionMapper.class);
  
  @ExceptionHandler(JsonProcessingRuntimeException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<Map<String, String>> handleJsonProcessingRuntimeException(JsonProcessingRuntimeException jsonProcessingRuntimeException) {
    LOGGER.error("JSON Processing error: {}", jsonProcessingRuntimeException.getMessage());
    Map<String, String> errorBody = Map.of("error", jsonProcessingRuntimeException.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
  }
}
