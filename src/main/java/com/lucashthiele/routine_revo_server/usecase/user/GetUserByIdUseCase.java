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
import com.lucashthiele.routine_revo_server.usecase.user.input.GetUserByIdInput;
import com.lucashthiele.routine_revo_server.usecase.user.output.AssignedRoutineOutput;
import com.lucashthiele.routine_revo_server.usecase.user.output.UserOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class GetUserByIdUseCase implements UseCaseInterface<UserOutput, GetUserByIdInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetUserByIdUseCase.class);
  
  private final UserGateway userGateway;
  private final WorkoutGateway workoutGateway;
  private final RoutineGateway routineGateway;

  public GetUserByIdUseCase(UserGateway userGateway, WorkoutGateway workoutGateway, RoutineGateway routineGateway) {
    this.userGateway = userGateway;
    this.workoutGateway = workoutGateway;
    this.routineGateway = routineGateway;
  }

  @Override
  public UserOutput execute(GetUserByIdInput input) {
    LOGGER.info("[GetUserByIdUseCase] Getting user with ID: {}", input.userId());
    
    User user = userGateway.findById(input.userId())
        .orElseThrow(() -> new UserNotFoundException(input.userId()));
    
    LOGGER.info("[GetUserByIdUseCase] User found: {}", input.userId());
    
    return enrichUserWithStats(user);
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
