package com.lucashthiele.routine_revo_server.usecase.user;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.shared.PaginatedResult;
import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.domain.user.UserFilter;
import com.lucashthiele.routine_revo_server.domain.workout.WorkoutSession;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.gateway.WorkoutGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.user.input.SearchMembersInput;
import com.lucashthiele.routine_revo_server.usecase.user.output.AssignedRoutineOutput;
import com.lucashthiele.routine_revo_server.usecase.user.output.ListUsersOutput;
import com.lucashthiele.routine_revo_server.usecase.user.output.UserOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SearchMembersUseCase implements UseCaseInterface<ListUsersOutput, SearchMembersInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SearchMembersUseCase.class);
  
  private final UserGateway userGateway;
  private final WorkoutGateway workoutGateway;
  private final RoutineGateway routineGateway;
  
  public SearchMembersUseCase(UserGateway userGateway, WorkoutGateway workoutGateway, RoutineGateway routineGateway) {
    this.userGateway = userGateway;
    this.workoutGateway = workoutGateway;
    this.routineGateway = routineGateway;
  }
  
  @Override
  public ListUsersOutput execute(SearchMembersInput input) {
    LOGGER.info("[SearchMembersUseCase] Searching for active members with filter: {}", input.name());
    
    // Create filter for MEMBER role and ACTIVE status
    UserFilter filter = new UserFilter(
        input.name(),
        Role.MEMBER,
        Status.ACTIVE
    );
    
    int page = input.page() != null ? input.page() : 0;
    int size = input.size() != null ? input.size() : 20;
    
    // Fetch users with filter
    PaginatedResult<User> paginatedUsers = userGateway.findAll(filter, page, size);
    
    // Convert to output with enriched stats
    List<UserOutput> userOutputs = paginatedUsers.items().stream()
        .map(this::enrichUserWithStats)
        .toList();
    
    LOGGER.info("[SearchMembersUseCase] Found {} active members", userOutputs.size());
    
    return new ListUsersOutput(
        userOutputs,
        paginatedUsers.total(),
        paginatedUsers.page(),
        paginatedUsers.size(),
        paginatedUsers.totalPages()
    );
  }
  
  private UserOutput enrichUserWithStats(User user) {
    UserOutput baseOutput = UserOutput.from(user);
    
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
