package com.lucashthiele.routine_revo_server.usecase.onboarding.exception;

public class InvalidOnboardingTokenException extends RuntimeException {
  public InvalidOnboardingTokenException(String message) {
    super(message);
  }
}
