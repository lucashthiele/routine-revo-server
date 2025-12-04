package com.lucashthiele.routine_revo_server.infrastructure.web.profile;

import com.lucashthiele.routine_revo_server.infrastructure.web.exception.GlobalExceptionHandler.ErrorResponse;
import com.lucashthiele.routine_revo_server.usecase.profile.exception.ProfileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ProfileExceptionMapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileExceptionMapper.class);

  @ExceptionHandler(ProfileNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleProfileNotFoundException(
      ProfileNotFoundException ex, WebRequest request) {
    
    LOGGER.error("Profile not found", ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.NOT_FOUND.value(),
        "Profile Not Found",
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
}

