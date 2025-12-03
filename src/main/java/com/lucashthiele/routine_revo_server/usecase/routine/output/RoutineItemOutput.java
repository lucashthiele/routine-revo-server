package com.lucashthiele.routine_revo_server.usecase.routine.output;

import java.util.UUID;

public record RoutineItemOutput(
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

