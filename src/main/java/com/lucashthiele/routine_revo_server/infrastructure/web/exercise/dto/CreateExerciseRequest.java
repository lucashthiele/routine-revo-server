package com.lucashthiele.routine_revo_server.infrastructure.web.exercise.dto;

import com.lucashthiele.routine_revo_server.domain.exercise.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateExerciseRequest(
    @NotBlank(message = "Name is required")
    String name,
    
    @NotNull(message = "Muscle group is required")
    MuscleGroup muscleGroup,
    
    String description,
    
    String equipment
) {
}

