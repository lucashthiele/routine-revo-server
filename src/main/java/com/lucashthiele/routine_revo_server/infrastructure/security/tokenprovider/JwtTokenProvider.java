package com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider;

import com.lucashthiele.routine_revo_server.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JwtTokenProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
  
  private final String secret;
  private final TokenProviderPurposeType purpose;
  
  public JwtTokenProvider(String secret, TokenProviderPurposeType purpose) {
    this.secret = secret;
    this.purpose = purpose;
    LOGGER.info("JwtTokenProvider created for purpose: {} with secret length: {}", 
        purpose.value(), secret != null ? secret.length() : 0);
  }

  String generateToken(User user, long expirationMs) {
    LOGGER.debug("Generating {} token for user: {}", purpose.value(), user.getEmail());
    
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + expirationMs);

    Map<String, Object> claims = new HashMap<>();
    claims.put("role", user.getRole().name());
    claims.put("purpose", purpose.value());
    
    String token = Jwts.builder()
        .subject(user.getEmail())
        .claims(claims)
        .issuedAt(now)
        .expiration(expirationDate)
        .signWith(getSigningKey(secret))
        .compact();
    
    LOGGER.debug("Token generated successfully for user: {}", user.getEmail());
    return token;
  }
  
  
  public Optional<String> validateToken(String token) {
    LOGGER.info("JwtTokenProvider - Validating token with expected purpose: {}", this.purpose.value());
    
    try {
      Claims claims = Jwts.parser()
          .verifyWith(this.getSigningKey(this.secret))
          .build()
          .parseSignedClaims(token)
          .getPayload();
      
      LOGGER.info("JwtTokenProvider - Token parsed successfully");
      LOGGER.info("JwtTokenProvider - Subject (email): {}", claims.getSubject());
      LOGGER.info("JwtTokenProvider - Issued at: {}", claims.getIssuedAt());
      LOGGER.info("JwtTokenProvider - Expiration: {}", claims.getExpiration());
      
      String tokenPurpose = claims.get("purpose", String.class);
      LOGGER.info("JwtTokenProvider - Token purpose: {}", tokenPurpose);
      
      if (!this.purpose.value().equals(tokenPurpose)) {
        LOGGER.warn("JwtTokenProvider - Token validation failed - Invalid token purpose. Expected: {}, Got: {}", 
            this.purpose.value(), tokenPurpose);
        return Optional.empty();
      }
      
      LOGGER.info("JwtTokenProvider - Token validated successfully for user: {}", claims.getSubject());
      return Optional.of(claims.getSubject());
    } catch (Exception e) {
      LOGGER.error("JwtTokenProvider - Token validation failed - Exception: {} - {}", e.getClass().getSimpleName(), e.getMessage());
      LOGGER.debug("JwtTokenProvider - Full exception trace", e);
      return Optional.empty();
    }
  }

  SecretKey getSigningKey(String secret) {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
