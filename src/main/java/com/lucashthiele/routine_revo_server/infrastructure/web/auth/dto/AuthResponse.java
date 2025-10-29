package com.lucashthiele.routine_revo_server.infrastructure.web.auth.dto;

import com.lucashthiele.routine_revo_server.usecase.auth.output.AuthOutput;

public record AuthResponse(String authToken, String refreshToken) {
  public static AuthResponse from(AuthOutput output) {
    return new AuthResponse(output.authToken(), output.refreshToken());
  }
}
