package com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider;

import com.lucashthiele.routine_revo_server.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ResetPasswordTokenProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordTokenProvider.class);
  
  private final JwtTokenProvider jwtTokenProvider;
  
  @Value("${jwt.password-reset-expiration-ms}")
  private long resetPasswordExpiration;

  public ResetPasswordTokenProvider(@Value("${jwt.password-reset-secret}") String passwordResetSecret) {
    this.jwtTokenProvider = new JwtTokenProvider(passwordResetSecret, TokenProviderPurposeType.RESET_PASSWORD);
    LOGGER.info("ResetPasswordTokenProvider initialized");
  }

  public String generateToken(User user) {
    return this.jwtTokenProvider.generateToken(user, resetPasswordExpiration);
  }
  
  public Optional<String> validateToken(String token) {
    return this.jwtTokenProvider.validateToken(token);
  }
}
