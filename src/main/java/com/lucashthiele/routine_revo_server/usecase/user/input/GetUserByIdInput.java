package com.lucashthiele.routine_revo_server.usecase.user.input;

import java.util.UUID;

public record GetUserByIdInput(UUID userId) {
  public GetUserByIdInput {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");
    }
  }
}
