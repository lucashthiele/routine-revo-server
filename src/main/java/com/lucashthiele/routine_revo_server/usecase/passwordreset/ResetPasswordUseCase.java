package com.lucashthiele.routine_revo_server.usecase.passwordreset;

import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.ResetPasswordTokenProvider;
import com.lucashthiele.routine_revo_server.usecase.auth.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.input.ResetPasswordInput;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordUseCase {
  
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
  
  public void execute(@Valid ResetPasswordInput input) {
    String email = resetPasswordTokenProvider.validateToken(input.token())
        .orElseThrow(() -> new InvalidResetTokenException("Token is invalid or expired."));
    
    String hashedPassword = passwordEncoder.encode(input.password());

    userGateway.updatePasswordByEmail(email, hashedPassword);
  }
}
