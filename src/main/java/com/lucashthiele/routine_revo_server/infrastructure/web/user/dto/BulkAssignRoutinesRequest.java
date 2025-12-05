package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BulkAssignRoutinesRequest(
    @NotEmpty(message = "At least one routine ID is required")
    List<UUID> routineIds,
    Instant expirationDate
) {
}

