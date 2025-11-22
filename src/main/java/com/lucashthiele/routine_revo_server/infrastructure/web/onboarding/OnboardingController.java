package com.lucashthiele.routine_revo_server.infrastructure.web.onboarding;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.infrastructure.web.onboarding.dto.ActivateAccountOnboardingRequest;
import com.lucashthiele.routine_revo_server.infrastructure.web.onboarding.dto.RequestOnboardingRequest;
import com.lucashthiele.routine_revo_server.usecase.onboarding.ActivateUserOnboardingUseCase;
import com.lucashthiele.routine_revo_server.usecase.onboarding.RequestOnboardingUseCase;
import com.lucashthiele.routine_revo_server.usecase.onboarding.ValidateOnboardingUseCase;
import com.lucashthiele.routine_revo_server.usecase.onboarding.input.ActivateUserOnboardingInput;
import com.lucashthiele.routine_revo_server.usecase.onboarding.input.RequestOnboardingInput;
import com.lucashthiele.routine_revo_server.usecase.onboarding.input.ValidateOnboardingInput;
import com.lucashthiele.routine_revo_server.usecase.passwordreset.exception.InvalidResetTokenException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/onboarding")
public class OnboardingController {
  private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingController.class);
  
  private final RequestOnboardingUseCase requestOnboardingUseCase;
  private final ValidateOnboardingUseCase validateOnboardingUseCase;
  private final ActivateUserOnboardingUseCase activateUserOnboardingUseCase;
  private final UserGateway userGateway;

  public OnboardingController(RequestOnboardingUseCase requestOnboardingUseCase,
                              UserGateway userGateway,
                              ValidateOnboardingUseCase validateOnboardingUseCase,
                              ActivateUserOnboardingUseCase activateUserOnboardingUseCase) {
    this.userGateway = userGateway;
    this.requestOnboardingUseCase = requestOnboardingUseCase;
    this.validateOnboardingUseCase = validateOnboardingUseCase;
    this.activateUserOnboardingUseCase = activateUserOnboardingUseCase;
  }
  
  // TODO - This will be removed, since the onboarding use case is called when the user is created by the admin
  @PostMapping("/request")
  public ResponseEntity<Void> requestOnboarding(@Valid @RequestBody RequestOnboardingRequest request) {
    LOGGER.info("POST /api/v1/onboarding/request - Onboarding` request received for email: {}", request.email());

    User user = userGateway.findByEmail(request.email()).orElseThrow();

    RequestOnboardingInput input = new RequestOnboardingInput(user);

    requestOnboardingUseCase.execute(input);
    
    LOGGER.info("POST /api/v1/onboarding/request - Request processed for email: {}", request.email());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/validate-onboarding")
  public ResponseEntity<Void> validateResetToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
    LOGGER.info("POST /api/v1/onboarding/validate-onboarding - Token validation request received");

    String token = extractTokenFromHeader(authorizationHeader);

    ValidateOnboardingInput input = new ValidateOnboardingInput(token);

    validateOnboardingUseCase.execute(input);

    LOGGER.info("POST /api/v1/onboarding/validate-onboarding - Token validation successful");
    return ResponseEntity.ok().build();
  }
  
  @PostMapping("/activate-account")
  public ResponseEntity<Void> activateAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                              @RequestBody ActivateAccountOnboardingRequest request) {
    LOGGER.info("POST /api/v1/onboarding/activate-account - Account activation received");

    String token = extractTokenFromHeader(authorizationHeader);

    ActivateUserOnboardingInput input = new ActivateUserOnboardingInput(token, request.password());

    activateUserOnboardingUseCase.execute(input);

    LOGGER.info("POST /api/v1/onboarding/activate-account - Account activation successful");
    return ResponseEntity.ok().build();
  }
  
  private String extractTokenFromHeader(String header) {

    if (header == null || !header.startsWith("Bearer ")) {
      throw new InvalidResetTokenException("Authorization header is missing.");
    }

    return header.substring(7);
  }
}
