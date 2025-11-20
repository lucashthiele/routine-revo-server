package com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider;

import com.lucashthiele.routine_revo_server.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OnboardingTokenProvider {
  
  private final JwtTokenProvider jwtTokenProvider;
  
  @Value("${jwt.onboarding-expiration-ms}")
  private long onboardingExpirationMs;

  public OnboardingTokenProvider(JwtTokenProvider jwtTokenProvider,
                                 @Value("${jwt.onboarding-secret}") String onboardingSecret ) {
    jwtTokenProvider.setPurpose(TokenProviderPurposeType.ONBOARDING);
    jwtTokenProvider.setSecret(onboardingSecret);
    this.jwtTokenProvider = jwtTokenProvider;
  }

  public String generateToken(User user) {
    return this.jwtTokenProvider.generateToken(user, onboardingExpirationMs);
  }

  public Optional<String> validateToken(String token) {
    return this.jwtTokenProvider.validateToken(token);
  }
}
