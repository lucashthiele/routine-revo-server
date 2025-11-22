package com.lucashthiele.routine_revo_server.usecase.onboarding.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message) {
    super(message);
  }
}
