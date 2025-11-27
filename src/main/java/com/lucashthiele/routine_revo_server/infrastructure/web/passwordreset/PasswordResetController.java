package com.lucashthiele.routine_revo_server.infrastructure.web.passwordreset;

import com.lucashthiele.routine_revo_server.infrastructure.web.passwordreset.dto.RequestPasswordResetRequest;
import com.lucashthiele.routine_revo_server.infrastructure.web.passwordreset.dto.ResetPasswordRequest;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.RequestPasswordResetUseCase;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.ResetPasswordUseCase;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.ValidateResetPasswordUseCase;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.input.RequestPasswordResetInput;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.input.ResetPasswordInput;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.input.ValidateResetTokenInput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/password-reset")
public class PasswordResetController {
  private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetController.class);
  
  private final ResetPasswordUseCase resetPasswordUseCase;
  private final RequestPasswordResetUseCase requestPasswordResetUseCase;
  private final ValidateResetPasswordUseCase validateResetPasswordUseCase;

  public PasswordResetController(ResetPasswordUseCase resetPasswordUseCase,
                                 RequestPasswordResetUseCase requestPasswordResetUseCase,
                                 ValidateResetPasswordUseCase validateResetPasswordUseCase) {
    this.resetPasswordUseCase = resetPasswordUseCase;
    this.requestPasswordResetUseCase = requestPasswordResetUseCase;
    this.validateResetPasswordUseCase = validateResetPasswordUseCase;
  }
  
  @PostMapping("/request")
  public ResponseEntity<Void> requestReset(@Valid @RequestBody RequestPasswordResetRequest request) {
    LOGGER.info("POST /api/v1/password-reset/request - Password reset request received for email: {}", request.email());
    
    RequestPasswordResetInput input = new RequestPasswordResetInput(request.email());
    
    requestPasswordResetUseCase.execute(input);
    
    LOGGER.info("POST /api/v1/password-reset/request - Request processed for email: {}", request.email());
    return ResponseEntity.ok().build();
  }
  
  @PostMapping("/validate-reset")
  public ResponseEntity<Void> validateResetToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
    LOGGER.info("POST /api/v1/password-reset/validate-reset - Token validation request received");
    
    String token = extractTokenFromHeader(authorizationHeader);

    ValidateResetTokenInput input = new ValidateResetTokenInput(token);

    validateResetPasswordUseCase.execute(input);
    
    LOGGER.info("POST /api/v1/password-reset/validate-reset - Token validation successful");
    return ResponseEntity.ok().build();
  }
  
  @Transactional
  @PostMapping("/new-password")
  ResponseEntity<Void> updatePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, 
                                      @Valid @RequestBody ResetPasswordRequest request) {
    LOGGER.info("POST /api/v1/password-reset/new-password - Password update request received");
    
    String token = extractTokenFromHeader(authorizationHeader);

    ResetPasswordInput input = new ResetPasswordInput(token, request.newPassword());
    
    resetPasswordUseCase.execute(input);
    
    LOGGER.info("POST /api/v1/password-reset/new-password - Password updated successfully");
    return ResponseEntity.ok().build();
  }
  
  private String extractTokenFromHeader(String header) {

    if (header == null || !header.startsWith("Bearer ")) {
      throw new InvalidResetTokenException("Authorization header is missing.");
    }
    
    return header.substring(7);
  }

}
