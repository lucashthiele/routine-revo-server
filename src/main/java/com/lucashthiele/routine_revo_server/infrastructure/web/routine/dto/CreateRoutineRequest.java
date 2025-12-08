package com.lucashthiele.routine_revo_server.infrastructure.web.routine.dto;

import com.lucashthiele.routine_revo_server.domain.routine.RoutineType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateRoutineRequest(
    @NotBlank(message = "Name is required")
    String name,
    
    String description,
    
    Instant expirationDate,
    
    @NotNull(message = "Creator ID is required")
    UUID creatorId,
    
    UUID memberId,
    
    RoutineType routineType,
    
    @Valid
    List<RoutineItemRequest> items
) {
}

