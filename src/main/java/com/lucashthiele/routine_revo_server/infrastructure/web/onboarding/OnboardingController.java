package com.lucashthiele.routine_revo_server.infrastructure.web.onboarding;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.infrastructure.web.onboarding.dto.RequestOnboardingRequest;
import com.lucashthiele.routine_revo_server.usecase.onboarding.RequestOnboardingUseCase;
import com.lucashthiele.routine_revo_server.usecase.onboarding.input.RequestOnboardingInput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/onboarding")
public class OnboardingController {
  private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingController.class);
  
  private final RequestOnboardingUseCase requestOnboardingUseCase;
  private final UserGateway userGateway;

  public OnboardingController(RequestOnboardingUseCase requestOnboardingUseCase,
                              UserGateway userGateway) {
    this.userGateway = userGateway;
    this.requestOnboardingUseCase = requestOnboardingUseCase;
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
}
