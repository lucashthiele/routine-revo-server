package com.lucashthiele.routine_revo_server.infrastructure.web.exercise.dto;

import java.util.List;

public record SearchExercisesResponse(
    List<ExerciseResponse> exercises,
    long total,
    int page,
    int size,
    int totalPages
) {
}

