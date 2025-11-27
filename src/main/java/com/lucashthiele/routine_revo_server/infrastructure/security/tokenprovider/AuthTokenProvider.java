package com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider;

import com.lucashthiele.routine_revo_server.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthTokenProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenProvider.class);
  
  private final JwtTokenProvider jwtTokenProvider;
  
  @Value("${jwt.auth-token-expiration-ms}")
  private long authTokenExpirationMs;

  @Value("${jwt.refresh-token-expiration-ms}")
  private long refreshTokenExpirationMs;

  public AuthTokenProvider(@Value("${jwt.secret}") String jwtSecret) {
    this.jwtTokenProvider = new JwtTokenProvider(jwtSecret, TokenProviderPurposeType.AUTHENTICATION);
    LOGGER.info("AuthTokenProvider initialized");
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
  
  public Optional<String> validateToken(String token) {
    LOGGER.info("AuthTokenProvider - Validating authentication token");
    Optional<String> result = this.jwtTokenProvider.validateToken(token);
    if (result.isPresent()) {
      LOGGER.info("AuthTokenProvider - Token is valid for user: {}", result.get());
    } else {
      LOGGER.warn("AuthTokenProvider - Token validation failed");
    }
    return result;
  }
}
