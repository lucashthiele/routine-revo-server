package com.lucashthiele.routine_revo_server.usecase.profile.output;

import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;

import java.util.UUID;

public record UserProfileOutput(
    UUID id,
    String name,
    String email,
    Role role,
    Status status,
    UUID coachId,
    Integer workoutPerWeek,
    Double adherenceRate
) {
  public static UserProfileOutput from(User user) {
    return new UserProfileOutput(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getRole(),
        user.getStatus(),
        user.getCoachId(),
        user.getWorkoutPerWeek(),
        user.getAdherenceRate()
    );
  }
}

