package com.lucashthiele.routine_revo_server.usecase.user.input;

import java.util.UUID;

public record InactivateUserInput(UUID userId) {
  public InactivateUserInput {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
  }
}

