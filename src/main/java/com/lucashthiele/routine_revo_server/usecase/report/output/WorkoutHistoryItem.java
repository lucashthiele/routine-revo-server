package com.lucashthiele.routine_revo_server.usecase.report.output;

import java.time.Instant;
import java.util.UUID;

public record WorkoutHistoryItem(
    UUID workoutSessionId,
    Instant date,
    String routineName,
    Long durationMinutes,
    boolean completed,
    boolean partiallyCompleted
) {
}

