package com.lucashthiele.routine_revo_server.infrastructure.web.member.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record LogWorkoutRequest(
    UUID routineId,
    
    @NotNull(message = "Started at timestamp is required")
    Instant startedAt,
    
    Instant endedAt,
    
    @NotEmpty(message = "At least one workout item is required")
    @Valid
    List<LogWorkoutItemRequest> items
) {
}

