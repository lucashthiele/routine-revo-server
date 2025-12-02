package com.lucashthiele.routine_revo_server.infrastructure.web.exercise.dto;

import java.util.UUID;

public record CreateExerciseResponse(
    UUID id,
    String message
) {
}

