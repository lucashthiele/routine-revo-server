package com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider;

import com.lucashthiele.routine_revo_server.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ResetPasswordTokenProvider {
  
  private final JwtTokenProvider jwtTokenProvider;
  
  @Value("${jwt.password-reset-secret}")
  private String passwordResetSecret;

  @Value("${jwt.password-reset-expiration-ms}")
  private long resetPasswordExpiration;

  public ResetPasswordTokenProvider(JwtTokenProvider jwtTokenProvider) {
    jwtTokenProvider.setPurpose(TokenProviderPurposeType.RESET_PASSWORD);
    jwtTokenProvider.setSecret(passwordResetSecret);
    this.jwtTokenProvider = jwtTokenProvider;
    }

  public String generateToken(User user) {
    return this.jwtTokenProvider.generateToken(user, resetPasswordExpiration);
  }
  
  public Optional<String> validateToken(String token) {
    return this.jwtTokenProvider.validateToken(token);
  }
}
