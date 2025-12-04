package com.lucashthiele.routine_revo_server.infrastructure.web.member.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LogWorkoutItemRequest(
    @NotNull(message = "Exercise ID is required")
    UUID exerciseId,
    
    @NotNull(message = "Sets done is required")
    @Min(value = 1, message = "Sets done must be at least 1")
    Integer setsDone,
    
    @NotNull(message = "Reps done is required")
    @Min(value = 1, message = "Reps done must be at least 1")
    Integer repsDone,
    
    @Min(value = 0, message = "Load used must be non-negative")
    Double loadUsed
) {
}

