package com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider;

import com.lucashthiele.routine_revo_server.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtTokenProvider {
  @Setter
  private String secret;
  @Setter
  private TokenProviderPurposeType purpose;

  String generateToken(User user, long expirationMs) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + expirationMs);

    Map<String, Object> claims = new HashMap<>();
    claims.put("role", user.getRole().name());
    claims.put("purpose", purpose.value());
    
    return Jwts.builder()
        .subject(user.getEmail())
        .claims(claims)
        .issuedAt(now)
        .expiration(expirationDate)
        .signWith(getSigningKey(secret))
        .compact();
  }
  
  
  public Optional<String> validateToken(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(this.getSigningKey(this.secret))
          .build()
          .parseSignedClaims(token)
          .getPayload();
      
      String purpose = claims.get("purpose", String.class);
      if (!TokenProviderPurposeType.RESET_PASSWORD.value().equals(purpose)) {
        return Optional.empty();
      }
      
      return Optional.of(claims.getSubject());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  SecretKey getSigningKey(String secret) {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
