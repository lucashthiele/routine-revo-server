package com.lucashthiele.routine_revo_server.infrastructure.web.member;

import com.lucashthiele.routine_revo_server.usecase.workout.exception.MemberNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MemberExceptionMapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(MemberExceptionMapper.class);
  
  @ExceptionHandler(MemberNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleMemberNotFoundException(MemberNotFoundException ex) {
    LOGGER.warn("MemberNotFoundException: {}", ex.getMessage());
    
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", HttpStatus.NOT_FOUND.value());
    body.put("error", "Not Found");
    body.put("message", ex.getMessage());
    
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }
}

