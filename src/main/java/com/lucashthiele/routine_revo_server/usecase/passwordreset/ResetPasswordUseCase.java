package com.lucashthiele.routine_revo_server.usecase.passwordreset;

import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.ResetPasswordTokenProvider;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.input.ResetPasswordInput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordUseCase implements UseCaseInterface<Void, ResetPasswordInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ResetPasswordUseCase.class);
  
  private final ResetPasswordTokenProvider resetPasswordTokenProvider;
  private final PasswordEncoder passwordEncoder;
  private final UserGateway userGateway;
  
  public ResetPasswordUseCase(ResetPasswordTokenProvider resetPasswordTokenProvider,
                              PasswordEncoder passwordEncoder,
                              UserGateway userGateway) {
    this.resetPasswordTokenProvider = resetPasswordTokenProvider;
    this.passwordEncoder = passwordEncoder;
    this.userGateway = userGateway;
  }
  
  public Void execute(@Valid ResetPasswordInput input) {
    LOGGER.info("Password reset attempt with token");
    
    String email = resetPasswordTokenProvider.validateToken(input.token())
        .orElseThrow(() -> {
          LOGGER.warn("Password reset failed - Invalid or expired token");
          return new InvalidResetTokenException("Token is invalid or expired.");
        });
    
    String hashedPassword = passwordEncoder.encode(input.password());

    userGateway.updatePasswordByEmail(email, hashedPassword);
    
    LOGGER.info("Password successfully reset for user: {}", email);
    
    return null;
  }
}
