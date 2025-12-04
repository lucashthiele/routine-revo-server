package com.lucashthiele.routine_revo_server.infrastructure.web.auth.dto;

import com.lucashthiele.routine_revo_server.usecase.auth.output.AuthUserOutput;

public record AuthUserResponse(
    String id,
    String email,
    String name,
    String role
) {
  public static AuthUserResponse from(AuthUserOutput output) {
    return new AuthUserResponse(
        output.id(),
        output.email(),
        output.name(),
        output.role()
    );
  }
}

