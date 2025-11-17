package com.lucashthiele.routine_revo_server.usecase.auth;

import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.AuthTokenProvider;
import com.lucashthiele.routine_revo_server.usecase.auth.exception.InvalidCredentialsException;
import com.lucashthiele.routine_revo_server.usecase.auth.input.AuthInput;
import com.lucashthiele.routine_revo_server.usecase.auth.output.AuthOutput;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticateUserUseCase {
  
  private final UserGateway userGateway;
  private final PasswordEncoder passwordEncoder;
  private final AuthTokenProvider jwtTokenProvider;

  private static final String ERROR_MESSAGE = "Invalid email or password combination.";

  public AuthenticateUserUseCase(
      UserGateway userGateway,
      PasswordEncoder passwordEncoder,
      AuthTokenProvider jwtTokenProvider
  ) {
    this.userGateway = userGateway;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }
  
  public AuthOutput execute(AuthInput input) {
    validateInput(input);

    // Get user from db
    Optional<User> userOptional = userGateway.findByEmail(input.email());
    
    // Validate password
    User user = userOptional
        .filter(u -> passwordEncoder.matches(input.password(), u.getPassword()))
        .orElseThrow(() -> new InvalidCredentialsException(ERROR_MESSAGE));
    
    if (user.getStatus() != Status.ACTIVE) {
      throw new InvalidCredentialsException(ERROR_MESSAGE);
    }
    
    String authToken = jwtTokenProvider.generateAuthToken(user);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user);
    
    return new AuthOutput(authToken, refreshToken);
  }
  
  private void validateInput(AuthInput input) {
    if (input.email() == null || input.email().isBlank() ||
        input.password() == null || input.password().isBlank()) {
      throw new InvalidCredentialsException(ERROR_MESSAGE);
    }
  }
}
