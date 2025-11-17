package com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider;

import com.lucashthiele.routine_revo_server.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenProvider {
  
  private final JwtTokenProvider jwtTokenProvider;
  
  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.auth-token-expiration-ms}")
  private long authTokenExpirationMs;

  @Value("${jwt.refresh-token-expiration-ms}")
  private long refreshTokenExpirationMs;

  public AuthTokenProvider(JwtTokenProvider jwtTokenProvider) {
    jwtTokenProvider.setPurpose(TokenProviderPurposeType.AUTHENTICATION);
    jwtTokenProvider.setSecret(jwtSecret);
    this.jwtTokenProvider = jwtTokenProvider;
  }
  
  public String generateAuthToken(User user) {
    return generateToken(user, authTokenExpirationMs);
  }

  public String generateRefreshToken(User user) {
    return generateToken(user, refreshTokenExpirationMs);
  }

  private String generateToken(User user, long expirationMs) {
    return this.jwtTokenProvider.generateToken(user, expirationMs);
  }
}
