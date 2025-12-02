package com.lucashthiele.routine_revo_server.usecase.exercise.output;

import java.util.List;

public record SearchExercisesOutput(
    List<ExerciseOutput> exercises,
    long total,
    int page,
    int size,
    int totalPages
) {
}

