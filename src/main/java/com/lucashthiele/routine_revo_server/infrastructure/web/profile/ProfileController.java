package com.lucashthiele.routine_revo_server.infrastructure.web.profile;

import com.lucashthiele.routine_revo_server.infrastructure.security.SecurityContextHelper;
import com.lucashthiele.routine_revo_server.infrastructure.web.profile.dto.UpdateProfileRequest;
import com.lucashthiele.routine_revo_server.infrastructure.web.profile.dto.UserProfileResponse;
import com.lucashthiele.routine_revo_server.usecase.profile.GetMyProfileUseCase;
import com.lucashthiele.routine_revo_server.usecase.profile.UpdateMyProfileUseCase;
import com.lucashthiele.routine_revo_server.usecase.profile.input.GetMyProfileInput;
import com.lucashthiele.routine_revo_server.usecase.profile.input.UpdateMyProfileInput;
import com.lucashthiele.routine_revo_server.usecase.profile.output.UserProfileOutput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/me")
public class ProfileController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
  
  private final GetMyProfileUseCase getMyProfileUseCase;
  private final UpdateMyProfileUseCase updateMyProfileUseCase;
  private final SecurityContextHelper securityContextHelper;

  public ProfileController(
      GetMyProfileUseCase getMyProfileUseCase,
      UpdateMyProfileUseCase updateMyProfileUseCase,
      SecurityContextHelper securityContextHelper) {
    this.getMyProfileUseCase = getMyProfileUseCase;
    this.updateMyProfileUseCase = updateMyProfileUseCase;
    this.securityContextHelper = securityContextHelper;
  }

  @GetMapping
  public ResponseEntity<UserProfileResponse> getMyProfile() {
    UUID userId = securityContextHelper.getCurrentUserId();
    LOGGER.info("GET /api/v1/me - Fetching profile for user: {}", userId);
    
    GetMyProfileInput input = new GetMyProfileInput(userId);
    UserProfileOutput output = getMyProfileUseCase.execute(input);
    
    UserProfileResponse response = new UserProfileResponse(
        output.id(),
        output.name(),
        output.email(),
        output.role(),
        output.status(),
        output.coachId(),
        output.workoutPerWeek(),
        output.adherenceRate()
    );
    
    LOGGER.info("GET /api/v1/me - Profile returned for user: {}", userId);
    return ResponseEntity.ok(response);
  }

  @PutMapping
  public ResponseEntity<UserProfileResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
    UUID userId = securityContextHelper.getCurrentUserId();
    LOGGER.info("PUT /api/v1/me - Updating profile for user: {}", userId);
    
    UpdateMyProfileInput input = new UpdateMyProfileInput(userId, request.name());
    UserProfileOutput output = updateMyProfileUseCase.execute(input);
    
    UserProfileResponse response = new UserProfileResponse(
        output.id(),
        output.name(),
        output.email(),
        output.role(),
        output.status(),
        output.coachId(),
        output.workoutPerWeek(),
        output.adherenceRate()
    );
    
    LOGGER.info("PUT /api/v1/me - Profile updated for user: {}", userId);
    return ResponseEntity.ok(response);
  }
}

