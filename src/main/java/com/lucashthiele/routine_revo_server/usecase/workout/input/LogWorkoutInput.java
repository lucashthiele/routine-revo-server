package com.lucashthiele.routine_revo_server.usecase.workout.input;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LogWorkoutInput(
    UUID memberId,
    UUID routineId,
    Instant startedAt,
    Instant endedAt,
    List<LogWorkoutItemInput> items
) {
}

