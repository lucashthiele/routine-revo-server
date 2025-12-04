package com.lucashthiele.routine_revo_server.usecase.workout.input;

import java.util.UUID;

public record LogWorkoutItemInput(
    UUID exerciseId,
    Integer setsDone,
    Integer repsDone,
    Double loadUsed
) {
}

