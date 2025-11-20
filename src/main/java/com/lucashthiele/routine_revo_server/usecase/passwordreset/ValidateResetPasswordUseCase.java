package com.lucashthiele.routine_revo_server.usecase.passwordreset;

import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.ResetPasswordTokenProvider;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.input.ValidateResetTokenInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ValidateResetPasswordUseCase {
  private static final Logger LOGGER = LoggerFactory.getLogger(ValidateResetPasswordUseCase.class);
  
  private final ResetPasswordTokenProvider resetPasswordTokenProvider;

  public ValidateResetPasswordUseCase(ResetPasswordTokenProvider resetPasswordTokenProvider) {
    this.resetPasswordTokenProvider = resetPasswordTokenProvider;
  }

  public void execute(ValidateResetTokenInput input) {
    LOGGER.info("Validating password reset token");
    
    resetPasswordTokenProvider.validateToken(input.token())
        .orElseThrow(() -> {
          LOGGER.warn("Token validation failed - Invalid or expired token");
          return new InvalidResetTokenException("Token is invalid or expired.");
        });
    
    LOGGER.info("Password reset token validated successfully");
  }
}
