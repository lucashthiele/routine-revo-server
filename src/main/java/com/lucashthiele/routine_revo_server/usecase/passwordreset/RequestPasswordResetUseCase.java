package com.lucashthiele.routine_revo_server.usecase.passwordreset;

import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.ResetPasswordTokenProvider;
import com.lucashthiele.routine_revo_server.gateway.EmailGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.input.RequestPasswordResetInput;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequestPasswordResetUseCase implements UseCaseInterface<Void, RequestPasswordResetInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RequestPasswordResetUseCase.class);
  
  private final UserGateway userGateway;
  private final ResetPasswordTokenProvider jwtTokenProvider;
  private final EmailGateway emailGateway;

  public RequestPasswordResetUseCase(UserGateway userGateway,
                                     ResetPasswordTokenProvider jwtTokenProvider,
                                     EmailGateway emailGateway) {
    this.userGateway = userGateway;
    this.jwtTokenProvider = jwtTokenProvider;
    this.emailGateway = emailGateway;
  }
  
  public Void execute(RequestPasswordResetInput input) {
    LOGGER.info("Password reset requested for email: {}", input.email());
    
    if (input.email() == null || input.email().isBlank()) {
      LOGGER.warn("Password reset request failed - Empty email provided");
      return null;
    }

    Optional<User> userOptional = userGateway.findByEmail(input.email());
    
    if (userOptional.isPresent()) {
      User user = userOptional.get();

      if (user.getStatus() == Status.ACTIVE) {
        String resetToken = jwtTokenProvider.generateToken(user);

        emailGateway.sendPasswordResetEmail(user, resetToken);
        LOGGER.info("Password reset email initiated for user: {}", input.email());
      } else {
        LOGGER.warn("Password reset request for inactive user: {}", input.email());
      }
    } else {
      LOGGER.info("Password reset requested for non-existent email: {}", input.email());
    }
    
    return null;
  }
}
