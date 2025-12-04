package com.lucashthiele.routine_revo_server.usecase.workout.output;

import java.util.UUID;

public record LogWorkoutOutput(
    UUID workoutSessionId,
    Double newAdherenceRate,
    String message
) {
}

