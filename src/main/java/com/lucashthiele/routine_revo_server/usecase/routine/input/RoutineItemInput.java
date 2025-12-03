package com.lucashthiele.routine_revo_server.usecase.routine.input;

import java.util.UUID;

public record RoutineItemInput(
    UUID exerciseId,
    Integer sets,
    String reps,
    Double load,
    String restTime,
    Integer sequenceOrder
) {
}

