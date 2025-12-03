package com.lucashthiele.routine_revo_server.infrastructure.web.routine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UpdateRoutineRequest(
    @NotBlank(message = "Name is required")
    String name,
    
    String description,
    
    Instant expirationDate,
    
    UUID memberId,
    
    @Valid
    List<RoutineItemRequest> items
) {
}

