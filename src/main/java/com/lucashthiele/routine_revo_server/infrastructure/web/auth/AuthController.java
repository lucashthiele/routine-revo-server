package com.lucashthiele.routine_revo_server.infrastructure.web.auth;

import com.lucashthiele.routine_revo_server.infrastructure.web.auth.dto.AuthRequest;
import com.lucashthiele.routine_revo_server.infrastructure.web.auth.dto.AuthResponse;
import com.lucashthiele.routine_revo_server.usecase.auth.AuthenticateUserUseCase;
import com.lucashthiele.routine_revo_server.usecase.auth.input.AuthInput;
import com.lucashthiele.routine_revo_server.usecase.auth.output.AuthOutput;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  
  private final AuthenticateUserUseCase authenticateUserUseCase;

  public AuthController(AuthenticateUserUseCase authenticateUserUseCase) {
    this.authenticateUserUseCase = authenticateUserUseCase;
  }
  
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    AuthInput input = new AuthInput(request.email(), request.password());

    AuthOutput output = authenticateUserUseCase.execute(input);
    
    return ResponseEntity.ok(AuthResponse.from(output));
  }
}
