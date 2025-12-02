package com.lucashthiele.routine_revo_server.usecase.user.input;

import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;

public record ListUsersInput(
    String name,
    Role role,
    Status status,
    int page,
    int size
) {
  public ListUsersInput {
    if (page < 0) {
      throw new IllegalArgumentException("Page must be >= 0");
    }
    if (size <= 0) {
      throw new IllegalArgumentException("Size must be > 0");
    }
  }
}

