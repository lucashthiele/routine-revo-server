package com.lucashthiele.routine_revo_server.usecase.workout;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.domain.workout.WorkoutItem;
import com.lucashthiele.routine_revo_server.domain.workout.WorkoutSession;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.gateway.WorkoutGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.workout.exception.MemberNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.workout.input.LogWorkoutInput;
import com.lucashthiele.routine_revo_server.usecase.workout.output.LogWorkoutOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LogWorkoutUseCase implements UseCaseInterface<LogWorkoutOutput, LogWorkoutInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(LogWorkoutUseCase.class);
  
  private static final int DEFAULT_TARGET_WORKOUTS_PER_WEEK = 3;
  private static final int ADHERENCE_WINDOW_DAYS = 30;
  
  private final WorkoutGateway workoutGateway;
  private final UserGateway userGateway;
  
  public LogWorkoutUseCase(WorkoutGateway workoutGateway, UserGateway userGateway) {
    this.workoutGateway = workoutGateway;
    this.userGateway = userGateway;
  }
  
  @Override
  @Transactional
  public LogWorkoutOutput execute(LogWorkoutInput input) {
    LOGGER.info("[LogWorkoutUseCase] Logging workout for member: {}", input.memberId());
    
    // 1. Load the user to verify existence and get workout target
    User member = userGateway.findById(input.memberId())
        .orElseThrow(() -> new MemberNotFoundException(input.memberId()));
    
    // 2. Convert input to domain entity
    List<WorkoutItem> items = input.items() != null
        ? input.items().stream()
            .map(itemInput -> WorkoutItem.builder()
                .exerciseId(itemInput.exerciseId())
                .setsDone(itemInput.setsDone())
                .repsDone(itemInput.repsDone())
                .loadUsed(itemInput.loadUsed())
                .build())
            .collect(Collectors.toList())
        : List.of();
    
    WorkoutSession session = WorkoutSession.builder()
        .memberId(input.memberId())
        .routineId(input.routineId())
        .startedAt(input.startedAt() != null ? input.startedAt() : Instant.now())
        .endedAt(input.endedAt())
        .items(items)
        .build();
    
    // 3. Save the workout session
    UUID workoutSessionId = workoutGateway.save(session);
    LOGGER.info("[LogWorkoutUseCase] Workout session saved with ID: {}", workoutSessionId);
    
    // 4. Calculate and update adherence rate
    Double newAdherenceRate = calculateAndUpdateAdherence(member);
    
    LOGGER.info("[LogWorkoutUseCase] Adherence rate updated to: {}% for member: {}", 
        newAdherenceRate, input.memberId());
    
    return new LogWorkoutOutput(
        workoutSessionId,
        newAdherenceRate,
        "Workout logged successfully"
    );
  }
  
  private Double calculateAndUpdateAdherence(User member) {
    Instant now = Instant.now();
    
    // Determine the effective start of the adherence window
    // Use the later of: (30 days ago) OR (user creation date)
    Instant windowStart = now.minus(ADHERENCE_WINDOW_DAYS, ChronoUnit.DAYS);
    Instant effectiveStart = member.getCreatedAt() != null && member.getCreatedAt().isAfter(windowStart)
        ? member.getCreatedAt()
        : windowStart;
    
    // Count workouts since the effective start
    long totalWorkouts = workoutGateway.countWorkoutsByMemberSince(member.getId(), effectiveStart);
    
    // Get target per week (use user's setting or default)
    int targetPerWeek = member.getWorkoutPerWeek() != null 
        ? member.getWorkoutPerWeek() 
        : DEFAULT_TARGET_WORKOUTS_PER_WEEK;
    
    // Calculate pro-rated expected workouts based on actual days active
    long daysActive = ChronoUnit.DAYS.between(effectiveStart, now) + 1; // +1 to include today
    double weeksActive = daysActive / 7.0;
    double expectedWorkouts = targetPerWeek * weeksActive;
    
    // Calculate adherence rate (capped at 100%)
    double adherenceRate = expectedWorkouts > 0 
        ? Math.min(100.0, (totalWorkouts / expectedWorkouts) * 100.0)
        : 0.0;
    
    // Round to 2 decimal places
    adherenceRate = Math.round(adherenceRate * 100.0) / 100.0;
    
    LOGGER.debug("[LogWorkoutUseCase] Adherence calculation: {} workouts / {} expected ({} days active) = {}%",
        totalWorkouts, expectedWorkouts, daysActive, adherenceRate);
    
    // Update user with new adherence rate
    User updatedMember = User.builder()
        .id(member.getId())
        .name(member.getName())
        .email(member.getEmail())
        .password(member.getPassword())
        .role(member.getRole())
        .status(member.getStatus())
        .coachId(member.getCoachId())
        .workoutPerWeek(member.getWorkoutPerWeek())
        .adherenceRate(adherenceRate)
        .createdAt(member.getCreatedAt())
        .build();
    
    userGateway.update(updatedMember);
    
    return adherenceRate;
  }
}

