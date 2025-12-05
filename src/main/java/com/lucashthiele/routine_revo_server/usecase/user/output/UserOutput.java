package com.lucashthiele.routine_revo_server.usecase.user.output;

import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserOutput(
    UUID id,
    String name,
    String email,
    Role role,
    Status status,
    UUID coachId,
    Integer workoutPerWeek,
    Double adherenceRate,
    Instant lastWorkoutDate,
    List<AssignedRoutineOutput> assignedRoutines
) {
  public static UserOutput from(User user) {
    return new UserOutput(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getRole(),
        user.getStatus(),
        user.getCoachId(),
        user.getWorkoutPerWeek(),
        user.getAdherenceRate(),
        null,  // lastWorkoutDate - to be enriched by use case
        null   // assignedRoutines - to be enriched by use case
    );
  }
  
  public UserOutput withWorkoutStats(Instant lastWorkoutDate, List<AssignedRoutineOutput> assignedRoutines) {
    return new UserOutput(
        this.id,
        this.name,
        this.email,
        this.role,
        this.status,
        this.coachId,
        this.workoutPerWeek,
        this.adherenceRate,
        lastWorkoutDate,
        assignedRoutines
    );
  }
}
