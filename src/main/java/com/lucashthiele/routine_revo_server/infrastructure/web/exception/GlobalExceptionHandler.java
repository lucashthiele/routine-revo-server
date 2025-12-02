package com.lucashthiele.routine_revo_server.infrastructure.web.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {
    
    LOGGER.error("Validation error occurred", ex);
    
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Validation Failed",
        errors.toString(),
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(UnexpectedTypeException.class)
  public ResponseEntity<ErrorResponse> handleUnexpectedTypeException(
      UnexpectedTypeException ex, WebRequest request) {
    
    LOGGER.error("Validation configuration error", ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Validation Configuration Error",
        "Invalid validation constraint configuration. Please contact support.",
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    
    LOGGER.error("Constraint violation", ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Constraint Violation",
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(
      AuthenticationException ex, WebRequest request) {
    
    LOGGER.error("Authentication failed", ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.UNAUTHORIZED.value(),
        "Authentication Failed",
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {
    
    LOGGER.error("Access denied", ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.FORBIDDEN.value(),
        "Access Denied",
        "You don't have permission to access this resource",
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    
    LOGGER.error("Illegal argument", ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Invalid Request",
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ErrorResponse> handleNullPointerException(
      NullPointerException ex, WebRequest request) {
    
    LOGGER.error("Null pointer exception occurred", ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error",
        "A null value was encountered where it shouldn't be. Please contact support.",
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  @ExceptionHandler(JpaSystemException.class)
  public ResponseEntity<ErrorResponse> handleJpaSystemException(
      JpaSystemException ex, WebRequest request) {
    
    LOGGER.error("JPA system exception occurred", ex);
    
    String message = "Ocorreu um erro no banco de dados. Por favor, contate o suporte.";
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    
    // Check for specific database constraint violations
    if (ex.getMessage() != null) {
      String exceptionMessage = ex.getMessage();
      
      // Check for coach role constraint violation
      if (exceptionMessage.contains("The user referenced as coach_id must have the role COACH") ||
          exceptionMessage.contains("check_coach_role")) {
        message = "O usuário referenciado como treinador deve ter a função COACH.";
        status = HttpStatus.BAD_REQUEST;
      }
    }
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        status.value(),
        status == HttpStatus.BAD_REQUEST ? "Erro de Validação" : "Erro no Banco de Dados",
        message,
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex, WebRequest request) {
    
    LOGGER.error("Data integrity violation", ex);
    
    String message = "Data integrity violation occurred.";
    
    // Check for common constraint violations
    if (ex.getMessage() != null) {
      if (ex.getMessage().contains("unique constraint") || ex.getMessage().contains("duplicate key")) {
        message = "A record with this information already exists.";
      } else if (ex.getMessage().contains("not-null constraint")) {
        message = "Required information is missing.";
      } else if (ex.getMessage().contains("foreign key constraint")) {
        message = "Referenced record does not exist.";
      }
    }
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Data Integrity Violation",
        message,
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(InvalidCoachRoleException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCoachRoleException(
      InvalidCoachRoleException ex, WebRequest request) {
    
    LOGGER.error("Invalid coach role", ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.BAD_REQUEST.value(),
        "Erro de Validação",
        "O usuário referenciado como treinador deve ter a função COACH.",
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(com.lucashthiele.routine_revo_server.usecase.exercise.exception.ExerciseNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleExerciseNotFoundException(
      com.lucashthiele.routine_revo_server.usecase.exercise.exception.ExerciseNotFoundException ex, 
      WebRequest request) {
    
    LOGGER.error("Exercise not found", ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.NOT_FOUND.value(),
        "Exercise Not Found",
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
      Exception ex, WebRequest request) {
    
    LOGGER.error("Unexpected error occurred", ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
        LocalDateTime.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error",
        "An unexpected error occurred. Please try again later.",
        request.getDescription(false).replace("uri=", "")
    );
    
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  // Error response record
  public record ErrorResponse(
      LocalDateTime timestamp,
      int status,
      String error,
      String message,
      String path
  ) {}
}

