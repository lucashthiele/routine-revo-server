package com.lucashthiele.routine_revo_server.usecase.onboarding;

import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.OnboardingTokenProvider;
import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.ResetPasswordTokenProvider;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.onboarding.input.ValidateOnboardingInput;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.input.ValidateResetTokenInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ValidateOnboardingUseCase implements UseCaseInterface<Void, ValidateOnboardingInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ValidateOnboardingUseCase.class);
  
  private final OnboardingTokenProvider onboardingTokenProvider;

  public ValidateOnboardingUseCase(OnboardingTokenProvider onboardingTokenProvider) {
    this.onboardingTokenProvider = onboardingTokenProvider;
  }

  public Void execute(ValidateOnboardingInput input) {
    LOGGER.info("Validating onboarding token");

    onboardingTokenProvider.validateToken(input.token())
        .orElseThrow(() -> {
          LOGGER.warn("Token validation failed - Invalid or expired token");
          return new InvalidResetTokenException("Token is invalid or expired.");
        });
    
    LOGGER.info("Onboarding token validated successfully");
    
    return null;
  }
}
