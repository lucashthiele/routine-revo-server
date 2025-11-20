package com.lucashthiele.routine_revo_server.usecase.onboarding;

import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.gateway.EmailGateway;
import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.OnboardingTokenProvider;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.onboarding.input.RequestOnboardingInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequestOnboardingUseCase implements UseCaseInterface<Void, RequestOnboardingInput> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestOnboardingUseCase.class);
  private final OnboardingTokenProvider jwtTokenProvider;
  private final EmailGateway emailGateway;

  public RequestOnboardingUseCase(OnboardingTokenProvider onboardingTokenProvider, EmailGateway emailGateway) {
    this.jwtTokenProvider = onboardingTokenProvider;
    this.emailGateway = emailGateway;
  }

  @Override
  public Void execute(RequestOnboardingInput input) {
    var user = input.user();
    LOGGER.info("Onboarding requested for email: {}", user.getEmail());

    if (user.getEmail() == null || user.getEmail().isBlank()) {
      LOGGER.warn("Onboarding request failed - Empty email provided");
      return null;
    }

    if (user.getStatus() == Status.PENDING) {
      String resetToken = jwtTokenProvider.generateToken(user);

      emailGateway.sendOnboardingEmail(user, resetToken);
      LOGGER.info("Onboarding email initiated for user: {}", user.getEmail());
    } else {
      LOGGER.warn("Onboarding request for inactive user: {}", user.getEmail());
    }

    return null;
  }
}
