package com.lucashthiele.routine_revo_server.usecase.passwordreset.exception;

public class InvalidResetTokenException extends RuntimeException {
  public InvalidResetTokenException(String message) {
    super(message);
  }
}
