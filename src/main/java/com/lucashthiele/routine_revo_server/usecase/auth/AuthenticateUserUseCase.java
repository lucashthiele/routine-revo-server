package com.lucashthiele.routine_revo_server.usecase.auth;

import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.AuthTokenProvider;
import com.lucashthiele.routine_revo_server.usecase.auth.exception.InvalidCredentialsException;
import com.lucashthiele.routine_revo_server.usecase.auth.input.AuthInput;
import com.lucashthiele.routine_revo_server.usecase.auth.output.AuthOutput;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticateUserUseCase {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticateUserUseCase.class);
  
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
    LOGGER.info("Authentication attempt for email: {}", input.email());
    
    validateInput(input);

    // Get user from db
    Optional<User> userOptional = userGateway.findByEmail(input.email());
    
    // Validate password
    User user = userOptional
        .filter(u -> passwordEncoder.matches(input.password(), u.getPassword()))
        .orElseThrow(() -> {
          LOGGER.warn("Failed authentication attempt for email: {} - Invalid credentials", input.email());
          return new InvalidCredentialsException(ERROR_MESSAGE);
        });
    
    if (user.getStatus() != Status.ACTIVE) {
      LOGGER.warn("Failed authentication attempt for email: {} - User status is not ACTIVE", input.email());
      throw new InvalidCredentialsException(ERROR_MESSAGE);
    }
    
    String authToken = jwtTokenProvider.generateAuthToken(user);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user);
    
    LOGGER.info("User authenticated successfully: {}", input.email());
    return new AuthOutput(authToken, refreshToken);
  }
  
  private void validateInput(AuthInput input) {
    if (input.email() == null || input.email().isBlank() ||
        input.password() == null || input.password().isBlank()) {
      throw new InvalidCredentialsException(ERROR_MESSAGE);
    }
  }
}
