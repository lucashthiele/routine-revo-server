package com.lucashthiele.routine_revo_server.usecase.report;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.domain.workout.WorkoutSession;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.gateway.WorkoutGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.report.exception.MemberNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.report.input.GetPerformanceReportInput;
import com.lucashthiele.routine_revo_server.usecase.report.output.PerformanceReportOutput;
import com.lucashthiele.routine_revo_server.usecase.report.output.WorkoutHistoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GetPerformanceReportUseCase implements UseCaseInterface<PerformanceReportOutput, GetPerformanceReportInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetPerformanceReportUseCase.class);
  private static final int DEFAULT_WORKOUT_HISTORY_LIMIT = 20;
  
  private final UserGateway userGateway;
  private final WorkoutGateway workoutGateway;
  private final RoutineGateway routineGateway;

  public GetPerformanceReportUseCase(
      UserGateway userGateway,
      WorkoutGateway workoutGateway,
      RoutineGateway routineGateway) {
    this.userGateway = userGateway;
    this.workoutGateway = workoutGateway;
    this.routineGateway = routineGateway;
  }

  @Override
  public PerformanceReportOutput execute(GetPerformanceReportInput input) {
    LOGGER.info("[GetPerformanceReportUseCase] Generating performance report for member: {}", input.memberId());
    
    // Fetch the member and validate they exist and are a MEMBER role
    User member = userGateway.findById(input.memberId())
        .orElseThrow(() -> new MemberNotFoundException(input.memberId()));
    
    if (member.getRole() != Role.MEMBER) {
      LOGGER.warn("[GetPerformanceReportUseCase] User {} is not a MEMBER, role: {}", input.memberId(), member.getRole());
      throw new MemberNotFoundException(input.memberId());
    }
    
    // Fetch recent workout sessions
    List<WorkoutSession> recentWorkouts = workoutGateway.findRecentByMemberId(
        input.memberId(), 
        DEFAULT_WORKOUT_HISTORY_LIMIT
    );
    
    // Fetch all routines for this member to build a lookup map
    List<Routine> memberRoutines = routineGateway.findAllByMemberId(input.memberId());
    Map<UUID, Routine> routineMap = memberRoutines.stream()
        .collect(Collectors.toMap(Routine::getId, Function.identity()));
    
    // Build workout history items
    List<WorkoutHistoryItem> workoutHistory = recentWorkouts.stream()
        .map(session -> mapToHistoryItem(session, routineMap))
        .collect(Collectors.toList());
    
    LOGGER.info("[GetPerformanceReportUseCase] Report generated for member: {} with {} workout records", 
        input.memberId(), workoutHistory.size());
    
    return new PerformanceReportOutput(
        member.getId(),
        member.getName(),
        member.getEmail(),
        member.getAdherenceRate(),
        workoutHistory
    );
  }
  
  private WorkoutHistoryItem mapToHistoryItem(WorkoutSession session, Map<UUID, Routine> routineMap) {
    Routine routine = routineMap.get(session.getRoutineId());
    String routineName = routine != null ? routine.getName() : "Unknown Routine";
    
    // Calculate duration in minutes
    Long durationMinutes = null;
    if (session.getStartedAt() != null && session.getEndedAt() != null) {
      durationMinutes = Duration.between(session.getStartedAt(), session.getEndedAt()).toMinutes();
    }
    
    return new WorkoutHistoryItem(
        session.getId(),
        session.getEndedAt(),
        routineName,
        durationMinutes
    );
  }
}

