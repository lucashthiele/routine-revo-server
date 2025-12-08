package com.lucashthiele.routine_revo_server.infrastructure.web.report.dto;

import java.time.Instant;
import java.util.UUID;

public record WorkoutHistoryItemResponse(
    UUID workoutSessionId,
    Instant date,
    String routineName,
    Long durationMinutes,
    boolean completed,
    boolean partiallyCompleted
) {
}

