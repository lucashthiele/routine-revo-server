package com.lucashthiele.routine_revo_server.usecase.user;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.user.exception.UserNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.user.input.UpdateUserInput;
import com.lucashthiele.routine_revo_server.usecase.user.output.UserOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserUseCase implements UseCaseInterface<UserOutput, UpdateUserInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserUseCase.class);
  
  private final UserGateway userGateway;

  public UpdateUserUseCase(UserGateway userGateway) {
    this.userGateway = userGateway;
  }

  @Override
  public UserOutput execute(UpdateUserInput input) {
    LOGGER.info("[UpdateUserUseCase] Updating user with ID: {}", input.id());
    
    User existingUser = userGateway.findById(input.id())
        .orElseThrow(() -> new UserNotFoundException(input.id()));
    
    // Update only name and email - preserve role, status, password, etc.
    User updatedUser = User.builder()
        .id(existingUser.getId())
        .name(input.name())
        .email(input.email())
        .password(existingUser.getPassword())
        .role(existingUser.getRole())
        .status(existingUser.getStatus())
        .coachId(existingUser.getCoachId())
        .workoutPerWeek(existingUser.getWorkoutPerWeek())
        .build();
    
    User savedUser = userGateway.update(updatedUser);
    
    LOGGER.info("[UpdateUserUseCase] User updated successfully: {}", savedUser.getId());
    
    return UserOutput.from(savedUser);
  }
}

