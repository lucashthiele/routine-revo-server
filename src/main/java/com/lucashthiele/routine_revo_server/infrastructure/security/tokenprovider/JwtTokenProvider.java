package com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider;

import com.lucashthiele.routine_revo_server.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
  
  @Setter
  private String secret;
  @Setter
  private TokenProviderPurposeType purpose;

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
    LOGGER.debug("Validating token");
    
    try {
      Claims claims = Jwts.parser()
          .verifyWith(this.getSigningKey(this.secret))
          .build()
          .parseSignedClaims(token)
          .getPayload();
      
      String purpose = claims.get("purpose", String.class);
      if (!TokenProviderPurposeType.RESET_PASSWORD.value().equals(purpose)) {
        LOGGER.debug("Token validation failed - Invalid token purpose");
        return Optional.empty();
      }
      
      LOGGER.debug("Token validated successfully for user: {}", claims.getSubject());
      return Optional.of(claims.getSubject());
    } catch (Exception e) {
      LOGGER.debug("Token validation failed - Exception: {}", e.getMessage());
      return Optional.empty();
    }
  }

  SecretKey getSigningKey(String secret) {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
