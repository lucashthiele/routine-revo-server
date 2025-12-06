package com.lucashthiele.routine_revo_server.usecase.auth.output;

import com.lucashthiele.routine_revo_server.domain.user.User;

public record AuthUserOutput(
    String id,
    String email,
    String name,
    String role
) {
  public static AuthUserOutput from(User user) {
    return new AuthUserOutput(
        user.getId().toString(),
        user.getEmail(),
        user.getName(),
        user.getRole().name()
    );
  }
}


