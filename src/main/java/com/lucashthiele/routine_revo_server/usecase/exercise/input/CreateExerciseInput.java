package com.lucashthiele.routine_revo_server.usecase.exercise.input;

import com.lucashthiele.routine_revo_server.domain.exercise.MuscleGroup;

import java.io.InputStream;

public record CreateExerciseInput(
    String name,
    MuscleGroup muscleGroup,
    String description,
    String equipment,
    InputStream imageContent,
    String imageFileName,
    String imageContentType,
    long imageContentLength
) {
}

