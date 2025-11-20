package com.lucashthiele.routine_revo_server.usecase.passwordreset;

import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.ResetPasswordTokenProvider;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.input.ValidateResetTokenInput;
import org.springframework.stereotype.Service;

@Service
public class ValidateResetPasswordUseCase {
  
  private final ResetPasswordTokenProvider resetPasswordTokenProvider;

  public ValidateResetPasswordUseCase(ResetPasswordTokenProvider resetPasswordTokenProvider) {
    this.resetPasswordTokenProvider = resetPasswordTokenProvider;
  }

  public void execute(ValidateResetTokenInput input) {
    resetPasswordTokenProvider.validateToken(input.token())
        .orElseThrow(() -> new InvalidResetTokenException("Token is invalid or expired."));
  }
}
