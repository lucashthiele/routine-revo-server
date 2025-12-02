package com.lucashthiele.routine_revo_server.usecase.user;

import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.user.exception.UserNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.user.input.InactivateUserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InactivateUserUseCase implements UseCaseInterface<Void, InactivateUserInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(InactivateUserUseCase.class);
  
  private final UserGateway userGateway;

  public InactivateUserUseCase(UserGateway userGateway) {
    this.userGateway = userGateway;
  }

  @Override
  public Void execute(InactivateUserInput input) {
    LOGGER.info("[InactivateUserUseCase] Inactivating user with ID: {}", input.userId());
    
    User existingUser = userGateway.findById(input.userId())
        .orElseThrow(() -> new UserNotFoundException(input.userId()));
    
    // Soft delete - set status to INACTIVE
    User inactivatedUser = User.builder()
        .id(existingUser.getId())
        .name(existingUser.getName())
        .email(existingUser.getEmail())
        .password(existingUser.getPassword())
        .role(existingUser.getRole())
        .status(Status.INACTIVE)
        .coachId(existingUser.getCoachId())
        .workoutPerWeek(existingUser.getWorkoutPerWeek())
        .build();
    
    userGateway.update(inactivatedUser);
    
    LOGGER.info("[InactivateUserUseCase] User inactivated successfully: {}", input.userId());
    
    return null;
  }
}

