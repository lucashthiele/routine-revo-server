package com.lucashthiele.routine_revo_server.infrastructure.web.routine.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RoutineItemRequest(
    @NotNull(message = "Exercise ID is required")
    UUID exerciseId,
    
    @NotNull(message = "Sets is required")
    Integer sets,
    
    @NotNull(message = "Reps is required")
    String reps,
    
    Double load,
    
    String restTime,
    
    @NotNull(message = "Sequence order is required")
    Integer sequenceOrder
) {
}

