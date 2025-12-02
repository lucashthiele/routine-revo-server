package com.lucashthiele.routine_revo_server.usecase.exercise.input;

import com.lucashthiele.routine_revo_server.domain.exercise.MuscleGroup;

import java.io.InputStream;
import java.util.UUID;

public record UpdateExerciseInput(
    UUID id,
    String name,
    MuscleGroup muscleGroup,
    String description,
    String equipment,
    InputStream imageContent,
    String imageFileName,
    String imageContentType,
    Long imageContentLength
) {
}

