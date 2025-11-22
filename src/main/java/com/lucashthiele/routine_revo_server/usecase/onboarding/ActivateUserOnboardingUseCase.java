package com.lucashthiele.routine_revo_server.usecase.onboarding;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.StatusData;
import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.OnboardingTokenProvider;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.onboarding.exception.UserNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.onboarding.input.ActivateUserOnboardingInput;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ActivateUserOnboardingUseCase implements UseCaseInterface<Void, ActivateUserOnboardingInput> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ActivateUserOnboardingUseCase.class);

  private final OnboardingTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;
  private final UserGateway userGateway;

  public ActivateUserOnboardingUseCase(OnboardingTokenProvider jwtTokenProvider,
                                       PasswordEncoder passwordEncoder,
                                       UserGateway userGateway) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.passwordEncoder = passwordEncoder;
    this.userGateway = userGateway;
  }

  @Override
  public Void execute(ActivateUserOnboardingInput input) {
    LOGGER.info("Activating account with password");

    String email = jwtTokenProvider.validateToken(input.token())
        .orElseThrow(() -> {
          LOGGER.warn("Onboarding failed - Invalid or expired token");
          return new InvalidResetTokenException("Token is invalid or expired.");
        });

    LOGGER.info("Validating onboarding user");

    User user = userGateway.findUserByEmailAndStatus(email, StatusData.PENDING)
        .orElseThrow(() -> new UserNotFoundException("User not found with status pending."));

    String hashedPassword = passwordEncoder.encode(input.password());

    userGateway.activateUserAccountByEmail(user.getEmail(), hashedPassword);

    LOGGER.info("Account successfully activated for user: {}", user.getEmail());

    return null;
  }
}
