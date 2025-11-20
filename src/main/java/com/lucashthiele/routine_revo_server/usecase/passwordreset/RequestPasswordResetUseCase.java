package com.lucashthiele.routine_revo_server.usecase.passwordreset;

import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.ResetPasswordTokenProvider;
import com.lucashthiele.routine_revo_server.usecase.notification.EmailGateway;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.input.RequestPasswordResetInput;
import com.lucashthiele.routine_revo_server.usecase.user.UserGateway;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequestPasswordResetUseCase {
  
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
  
  public void execute(RequestPasswordResetInput input) {
    if (input.email() == null || input.email().isBlank()) {
      return;
    }

    Optional<User> userOptional = userGateway.findByEmail(input.email());
    
    if (userOptional.isPresent()) {
      User user = userOptional.get();

      if (user.getStatus() == Status.ACTIVE) {
        String resetToken = jwtTokenProvider.generateToken(user);

        emailGateway.sendPasswordResetEmail(user, resetToken);
      }
    }
  }
}
