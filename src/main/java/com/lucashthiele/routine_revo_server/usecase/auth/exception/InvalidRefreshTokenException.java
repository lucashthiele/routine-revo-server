package com.lucashthiele.routine_revo_server.usecase.auth.exception;

public class InvalidRefreshTokenException extends RuntimeException {
  public InvalidRefreshTokenException(String message) {
    super(message);
  }
}

