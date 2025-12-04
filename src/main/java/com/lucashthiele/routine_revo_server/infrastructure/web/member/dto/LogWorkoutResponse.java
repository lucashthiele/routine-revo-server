package com.lucashthiele.routine_revo_server.infrastructure.web.member.dto;

import java.util.UUID;

public record LogWorkoutResponse(
    UUID workoutSessionId,
    Double adherenceRate,
    String message
) {
}

