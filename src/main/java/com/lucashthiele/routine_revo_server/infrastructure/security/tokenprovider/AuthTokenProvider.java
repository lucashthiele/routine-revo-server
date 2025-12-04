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
  
  private final JwtTokenProvider authTokenProvider;
  private final JwtTokenProvider refreshTokenProvider;
  
  @Value("${jwt.auth-token-expiration-ms}")
  private long authTokenExpirationMs;

  @Value("${jwt.refresh-token-expiration-ms}")
  private long refreshTokenExpirationMs;

  public AuthTokenProvider(@Value("${jwt.secret}") String jwtSecret) {
    this.authTokenProvider = new JwtTokenProvider(jwtSecret, TokenProviderPurposeType.AUTHENTICATION);
    this.refreshTokenProvider = new JwtTokenProvider(jwtSecret, TokenProviderPurposeType.REFRESH);
    LOGGER.info("AuthTokenProvider initialized");
  }
  
  public String generateAuthToken(User user) {
    return this.authTokenProvider.generateToken(user, authTokenExpirationMs);
  }

  public String generateRefreshToken(User user) {
    return this.refreshTokenProvider.generateToken(user, refreshTokenExpirationMs);
  }
  
  public Optional<String> validateToken(String token) {
    LOGGER.info("AuthTokenProvider - Validating authentication token");
    Optional<String> result = this.authTokenProvider.validateToken(token);
    if (result.isPresent()) {
      LOGGER.info("AuthTokenProvider - Token is valid for user: {}", result.get());
    } else {
      LOGGER.warn("AuthTokenProvider - Token validation failed");
    }
    return result;
  }
  
  public Optional<String> validateRefreshToken(String token) {
    LOGGER.info("AuthTokenProvider - Validating refresh token");
    Optional<String> result = this.refreshTokenProvider.validateToken(token);
    if (result.isPresent()) {
      LOGGER.info("AuthTokenProvider - Refresh token is valid for user: {}", result.get());
    } else {
      LOGGER.warn("AuthTokenProvider - Refresh token validation failed");
    }
    return result;
  }
}
