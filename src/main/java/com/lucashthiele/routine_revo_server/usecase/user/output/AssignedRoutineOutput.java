package com.lucashthiele.routine_revo_server.usecase.user.output;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;

import java.time.Instant;
import java.util.UUID;

public record AssignedRoutineOutput(
    UUID id,
    String name,
    Instant expirationDate,
    Boolean isExpired
) {
  public static AssignedRoutineOutput from(Routine routine) {
    Instant now = Instant.now();
    boolean isExpired = routine.getExpirationDate() != null && routine.getExpirationDate().isBefore(now);
    
    return new AssignedRoutineOutput(
        routine.getId(),
        routine.getName(),
        routine.getExpirationDate(),
        isExpired
    );
  }
}

