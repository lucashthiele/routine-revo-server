package com.lucashthiele.routine_revo_server.usecase.exercise.input;

import com.lucashthiele.routine_revo_server.domain.exercise.MuscleGroup;

public record SearchExercisesInput(
    String name,
    MuscleGroup muscleGroup,
    int page,
    int size
) {
}

