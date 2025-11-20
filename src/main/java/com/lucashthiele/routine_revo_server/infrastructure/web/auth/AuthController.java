package com.lucashthiele.routine_revo_server.infrastructure.web.auth;

import com.lucashthiele.routine_revo_server.infrastructure.web.auth.dto.AuthRequest;
import com.lucashthiele.routine_revo_server.infrastructure.web.auth.dto.AuthResponse;
import com.lucashthiele.routine_revo_server.usecase.auth.AuthenticateUserUseCase;
import com.lucashthiele.routine_revo_server.usecase.auth.input.AuthInput;
import com.lucashthiele.routine_revo_server.usecase.auth.output.AuthOutput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
  
  private final AuthenticateUserUseCase authenticateUserUseCase;

  public AuthController(AuthenticateUserUseCase authenticateUserUseCase) {
    this.authenticateUserUseCase = authenticateUserUseCase;
  }
  
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    LOGGER.info("POST /api/v1/auth/login - Login request received for email: {}", request.email());
    
    AuthInput input = new AuthInput(request.email(), request.password());

    AuthOutput output = authenticateUserUseCase.execute(input);
    
    LOGGER.info("POST /api/v1/auth/login - Login successful for email: {}", request.email());
    return ResponseEntity.ok(AuthResponse.from(output));
  }
}
