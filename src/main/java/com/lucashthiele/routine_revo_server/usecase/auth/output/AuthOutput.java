package com.lucashthiele.routine_revo_server.usecase.auth.output;

import com.lucashthiele.routine_revo_server.domain.user.User;

public record AuthOutput(String authToken, String refreshToken, AuthUserOutput user) {
  public static AuthOutput of(String authToken, String refreshToken, User user) {
    return new AuthOutput(authToken, refreshToken, AuthUserOutput.from(user));
  }
}
