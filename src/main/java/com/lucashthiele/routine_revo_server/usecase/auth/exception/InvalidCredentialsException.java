package com.lucashthiele.routine_revo_server.usecase.auth.exception;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
