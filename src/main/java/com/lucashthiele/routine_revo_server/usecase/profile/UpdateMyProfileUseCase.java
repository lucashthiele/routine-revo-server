package com.lucashthiele.routine_revo_server.usecase.profile;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.profile.exception.ProfileNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.profile.input.UpdateMyProfileInput;
import com.lucashthiele.routine_revo_server.usecase.profile.output.UserProfileOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateMyProfileUseCase implements UseCaseInterface<UserProfileOutput, UpdateMyProfileInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateMyProfileUseCase.class);
  
  private final UserGateway userGateway;

  public UpdateMyProfileUseCase(UserGateway userGateway) {
    this.userGateway = userGateway;
  }

  @Override
  public UserProfileOutput execute(UpdateMyProfileInput input) {
    LOGGER.info("[UpdateMyProfileUseCase] Updating profile for user: {}", input.userId());
    
    User existingUser = userGateway.findById(input.userId())
        .orElseThrow(() -> new ProfileNotFoundException(input.userId()));
    
    // Only update allowed fields (name)
    // Preserve: role, status, password, email, coachId, workoutPerWeek, adherenceRate
    User updatedUser = User.builder()
        .id(existingUser.getId())
        .name(input.name())
        .email(existingUser.getEmail())
        .password(existingUser.getPassword())
        .role(existingUser.getRole())
        .status(existingUser.getStatus())
        .coachId(existingUser.getCoachId())
        .workoutPerWeek(existingUser.getWorkoutPerWeek())
        .adherenceRate(existingUser.getAdherenceRate())
        .build();
    
    User savedUser = userGateway.update(updatedUser);
    
    LOGGER.info("[UpdateMyProfileUseCase] Profile updated successfully for user: {}", savedUser.getId());
    
    return UserProfileOutput.from(savedUser);
  }
}

