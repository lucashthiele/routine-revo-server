package com.lucashthiele.routine_revo_server.infrastructure.web.exercise.dto;

import com.lucashthiele.routine_revo_server.domain.exercise.MuscleGroup;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExerciseResponse(
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

