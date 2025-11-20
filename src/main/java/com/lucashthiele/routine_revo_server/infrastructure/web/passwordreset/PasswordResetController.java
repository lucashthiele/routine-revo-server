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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/password-reset")
public class PasswordResetController {
  
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
    RequestPasswordResetInput input = new RequestPasswordResetInput(request.email());
    
    requestPasswordResetUseCase.execute(input);
    
    return ResponseEntity.ok().build();
  }
  
  @PostMapping("/validate-reset")
  public ResponseEntity<Void> validateResetToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
    String token = extractTokenFromHeader(authorizationHeader);

    ValidateResetTokenInput input = new ValidateResetTokenInput(token);

    validateResetPasswordUseCase.execute(input);
    
    return ResponseEntity.ok().build();
  }
  
  @PostMapping("/new-password")
  ResponseEntity<Void> updatePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, 
                                      @Valid @RequestBody ResetPasswordRequest request) {
    String token = extractTokenFromHeader(authorizationHeader);

    ResetPasswordInput input = new ResetPasswordInput(token, request.newPassword());
    
    resetPasswordUseCase.execute(input);
    
    return ResponseEntity.ok().build();
  }
  
  private String extractTokenFromHeader(String header) {

    if (header == null || !header.startsWith("Bearer ")) {
      throw new InvalidResetTokenException("Authorization header is missing.");
    }
    
    return header.substring(7);
  }

}
