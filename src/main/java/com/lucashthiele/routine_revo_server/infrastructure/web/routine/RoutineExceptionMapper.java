package com.lucashthiele.routine_revo_server.infrastructure.web.routine;

import com.lucashthiele.routine_revo_server.usecase.routine.exception.RoutineNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RoutineExceptionMapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(RoutineExceptionMapper.class);
  
  @ExceptionHandler(RoutineNotFoundException.class)
  public ProblemDetail handleRoutineNotFoundException(RoutineNotFoundException ex) {
    LOGGER.error("Routine not found: {}", ex.getMessage());
    
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND,
        ex.getMessage()
    );
    problemDetail.setTitle("Routine Not Found");
    
    return problemDetail;
  }
}

