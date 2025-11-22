package com.lucashthiele.routine_revo_server.usecase.onboarding;

import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.StatusData;
import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.OnboardingTokenProvider;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.onboarding.exception.UserNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.onboarding.input.ValidateOnboardingInput;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ValidateOnboardingUseCase implements UseCaseInterface<Void, ValidateOnboardingInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ValidateOnboardingUseCase.class);
  
  private final OnboardingTokenProvider onboardingTokenProvider;
  private final UserGateway userGateway;

  public ValidateOnboardingUseCase(OnboardingTokenProvider onboardingTokenProvider, UserGateway userGateway) {
    this.userGateway = userGateway;
    this.onboardingTokenProvider = onboardingTokenProvider;
  }

  public Void execute(ValidateOnboardingInput input) {
    LOGGER.info("Validating onboarding token");

    String email = onboardingTokenProvider.validateToken(input.token())
        .orElseThrow(() -> {
          LOGGER.warn("Token validation failed - Invalid or expired token");
          return new InvalidResetTokenException("Token is invalid or expired.");
        });

    userGateway.findUserByEmailAndStatus(email, StatusData.PENDING)
        .orElseThrow(() -> new UserNotFoundException("User not found with status pending."));

    LOGGER.info("Onboarding token validated successfully");
    
    return null;
  }
}
