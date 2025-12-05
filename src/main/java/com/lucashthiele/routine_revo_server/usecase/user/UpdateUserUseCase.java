package com.lucashthiele.routine_revo_server.usecase.user;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.domain.workout.WorkoutSession;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.gateway.WorkoutGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.user.exception.UserNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.user.input.UpdateUserInput;
import com.lucashthiele.routine_revo_server.usecase.user.output.AssignedRoutineOutput;
import com.lucashthiele.routine_revo_server.usecase.user.output.UserOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UpdateUserUseCase implements UseCaseInterface<UserOutput, UpdateUserInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserUseCase.class);
  
  private final UserGateway userGateway;
  private final WorkoutGateway workoutGateway;
  private final RoutineGateway routineGateway;

  public UpdateUserUseCase(UserGateway userGateway, WorkoutGateway workoutGateway, RoutineGateway routineGateway) {
    this.userGateway = userGateway;
    this.workoutGateway = workoutGateway;
    this.routineGateway = routineGateway;
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
        .adherenceRate(existingUser.getAdherenceRate())
        .build();
    
    User savedUser = userGateway.update(updatedUser);
    
    LOGGER.info("[UpdateUserUseCase] User updated successfully: {}", savedUser.getId());
    
    return enrichUserWithStats(savedUser);
  }
  
  private UserOutput enrichUserWithStats(User user) {
    UserOutput baseOutput = UserOutput.from(user);
    
    // Only fetch workout stats for MEMBER role
    if (user.getRole() != Role.MEMBER) {
      return baseOutput;
    }
    
    // Fetch last workout date
    Instant lastWorkoutDate = null;
    List<WorkoutSession> recentWorkouts = workoutGateway.findRecentByMemberId(user.getId(), 1);
    if (!recentWorkouts.isEmpty()) {
      WorkoutSession lastWorkout = recentWorkouts.get(0);
      lastWorkoutDate = lastWorkout.getEndedAt() != null ? lastWorkout.getEndedAt() : lastWorkout.getStartedAt();
    }
    
    // Fetch assigned routines
    List<Routine> routines = routineGateway.findAllByMemberId(user.getId());
    List<AssignedRoutineOutput> assignedRoutines = routines.stream()
        .map(AssignedRoutineOutput::from)
        .toList();
    
    return baseOutput.withWorkoutStats(lastWorkoutDate, assignedRoutines);
  }
}
