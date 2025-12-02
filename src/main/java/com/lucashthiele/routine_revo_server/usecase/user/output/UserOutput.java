package com.lucashthiele.routine_revo_server.usecase.user.output;

import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;

import java.util.UUID;

public record UserOutput(
    UUID id,
    String name,
    String email,
    Role role,
    Status status,
    UUID coachId,
    Integer workoutPerWeek
) {
  public static UserOutput from(User user) {
    return new UserOutput(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getRole(),
        user.getStatus(),
        user.getCoachId(),
        user.getWorkoutPerWeek()
    );
  }
}

