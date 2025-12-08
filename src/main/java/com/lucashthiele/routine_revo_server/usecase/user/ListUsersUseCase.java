package com.lucashthiele.routine_revo_server.usecase.user;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.shared.PaginatedResult;
import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.domain.user.UserFilter;
import com.lucashthiele.routine_revo_server.domain.workout.WorkoutSession;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.gateway.WorkoutGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.user.input.ListUsersInput;
import com.lucashthiele.routine_revo_server.usecase.user.output.AssignedRoutineOutput;
import com.lucashthiele.routine_revo_server.usecase.user.output.ListUsersOutput;
import com.lucashthiele.routine_revo_server.usecase.user.output.UserOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ListUsersUseCase implements UseCaseInterface<ListUsersOutput, ListUsersInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ListUsersUseCase.class);
  
  private final UserGateway userGateway;
  private final WorkoutGateway workoutGateway;
  private final RoutineGateway routineGateway;

  public ListUsersUseCase(UserGateway userGateway, WorkoutGateway workoutGateway, RoutineGateway routineGateway) {
    this.userGateway = userGateway;
    this.workoutGateway = workoutGateway;
    this.routineGateway = routineGateway;
  }

  @Override
  public ListUsersOutput execute(ListUsersInput input) {
    LOGGER.info("[ListUsersUseCase] Listing users with filter: {}, page: {}, size: {}", 
        input, input.page(), input.size());
    
    UserFilter filter = new UserFilter(input.name(), input.role(), input.status(), input.coachId());
    
    PaginatedResult<User> result = userGateway.findAll(filter, input.page(), input.size());
    
    List<UserOutput> userOutputs = result.items().stream()
        .map(user -> enrichUserWithStats(user))
        .toList();
    
    LOGGER.info("[ListUsersUseCase] Found {} users out of {} total", 
        userOutputs.size(), result.total());
    
    return new ListUsersOutput(
        userOutputs,
        result.total(),
        result.page(),
        result.size(),
        result.totalPages()
    );
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
