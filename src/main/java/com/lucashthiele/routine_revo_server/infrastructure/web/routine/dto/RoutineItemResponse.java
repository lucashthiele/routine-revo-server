package com.lucashthiele.routine_revo_server.infrastructure.web.routine.dto;

import java.util.UUID;

public record RoutineItemResponse(
    UUID id,
    UUID exerciseId,
    String exerciseName,
    String exerciseImageUrl,
    Integer sets,
    String reps,
    Double load,
    String restTime,
    Integer sequenceOrder
) {
}

