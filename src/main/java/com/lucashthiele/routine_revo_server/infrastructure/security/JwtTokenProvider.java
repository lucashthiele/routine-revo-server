package com.lucashthiele.routine_revo_server.infrastructure.security;
import com.lucashthiele.routine_revo_server.domain.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.auth-token-expiration-ms}")
  private long authTokenExpirationMs;

  @Value("${jwt.refresh-token-expiration-ms}")
  private long refreshTokenExpirationMs;
  
  public String generateAuthToken(User user) {
    return generateToken(user, authTokenExpirationMs);
  }
  
  public String generateRefreshToken(User user) {
    return generateToken(user, refreshTokenExpirationMs);
  }

  private String generateToken(User user, long expirationMs) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + expirationMs);

    Map<String, Object> claims = new HashMap<>();
    claims.put("role", user.getRole().name());
    
    return Jwts.builder()
        .subject(user.getEmail())
        .claims(claims)
        .issuedAt(now)
        .expiration(expirationDate)
        .signWith(getSigningKey())
        .compact();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
