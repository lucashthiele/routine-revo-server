package com.lucashthiele.routine_revo_server.usecase.auth;

import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider.AuthTokenProvider;
import com.lucashthiele.routine_revo_server.usecase.auth.exception.InvalidRefreshTokenException;
import com.lucashthiele.routine_revo_server.usecase.auth.input.RefreshTokenInput;
import com.lucashthiele.routine_revo_server.usecase.auth.output.AuthOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RefreshTokenUseCase {
  private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenUseCase.class);
  
  private static final String ERROR_MESSAGE = "Refresh token inválido ou expirado.";
  
  private final UserGateway userGateway;
  private final AuthTokenProvider authTokenProvider;

  public RefreshTokenUseCase(UserGateway userGateway, AuthTokenProvider authTokenProvider) {
    this.userGateway = userGateway;
    this.authTokenProvider = authTokenProvider;
  }
  
  public AuthOutput execute(RefreshTokenInput input) {
    LOGGER.info("Refresh token attempt");
    
    if (input.refreshToken() == null || input.refreshToken().isBlank()) {
      LOGGER.warn("Refresh token is missing or empty");
      throw new InvalidRefreshTokenException(ERROR_MESSAGE);
    }
    
    Optional<String> emailOptional = authTokenProvider.validateRefreshToken(input.refreshToken());
    
    if (emailOptional.isEmpty()) {
      LOGGER.warn("Refresh token validation failed");
      throw new InvalidRefreshTokenException(ERROR_MESSAGE);
    }
    
    String email = emailOptional.get();
    LOGGER.info("Refresh token validated for email: {}", email);
    
    Optional<User> userOptional = userGateway.findByEmail(email);
    
    User user = userOptional
        .filter(u -> u.getStatus() == Status.ACTIVE)
        .orElseThrow(() -> {
          LOGGER.warn("User not found or inactive for email: {}", email);
          return new InvalidRefreshTokenException(ERROR_MESSAGE);
        });
    
    String newAuthToken = authTokenProvider.generateAuthToken(user);
    String newRefreshToken = authTokenProvider.generateRefreshToken(user);
    
    LOGGER.info("New tokens generated successfully for email: {}", email);
    return new AuthOutput(newAuthToken, newRefreshToken);
  }
}

