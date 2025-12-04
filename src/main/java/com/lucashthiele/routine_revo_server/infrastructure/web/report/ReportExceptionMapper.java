package com.lucashthiele.routine_revo_server.infrastructure.web.report;

import com.lucashthiele.routine_revo_server.infrastructure.web.exception.GlobalExceptionHandler.ErrorResponse;
import com.lucashthiele.routine_revo_server.usecase.report.exception.MemberNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ReportExceptionMapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReportExceptionMapper.class);

  @ExceptionHandler(MemberNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleMemberNotFoundException(
      MemberNotFoundException ex, WebRequest request) {
    
    LOGGER.error("Member not found for report", ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.NOT_FOUND.value(),
        "Member Not Found",
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
}

