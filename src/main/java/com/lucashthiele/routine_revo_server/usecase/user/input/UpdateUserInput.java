package com.lucashthiele.routine_revo_server.usecase.user.input;

import java.util.UUID;

public record UpdateUserInput(
    UUID id,
    String name,
    String email
) {
  public UpdateUserInput {
    if (id == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email cannot be null or blank");
    }
  }
}

