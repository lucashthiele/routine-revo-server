package com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider;

import com.lucashthiele.routine_revo_server.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OnboardingTokenProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingTokenProvider.class);
  
  private final JwtTokenProvider jwtTokenProvider;
  
  @Value("${jwt.onboarding-expiration-ms}")
  private long onboardingExpirationMs;

  public OnboardingTokenProvider(@Value("${jwt.onboarding-secret}") String onboardingSecret) {
    this.jwtTokenProvider = new JwtTokenProvider(onboardingSecret, TokenProviderPurposeType.ONBOARDING);
    LOGGER.info("OnboardingTokenProvider initialized");
  }

  public String generateToken(User user) {
    return this.jwtTokenProvider.generateToken(user, onboardingExpirationMs);
  }

  public Optional<String> validateToken(String token) {
    return this.jwtTokenProvider.validateToken(token);
  }
}
