package com.lucashthiele.routine_revo_server.usecase.exercise.output;

import com.lucashthiele.routine_revo_server.domain.exercise.MuscleGroup;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExerciseOutput(
    UUID id,
    String name,
    MuscleGroup muscleGroup,
    String description,
    String equipment,
    String imageUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

