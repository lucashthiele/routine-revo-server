package com.lucashthiele.routine_revo_server.usecase.profile.exception;

import java.util.UUID;

public class ProfileNotFoundException extends RuntimeException {
  public ProfileNotFoundException(UUID userId) {
    super("Profile not found for user ID: " + userId);
  }
}

