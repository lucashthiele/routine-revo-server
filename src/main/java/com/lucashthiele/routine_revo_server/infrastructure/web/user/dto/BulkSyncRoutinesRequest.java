package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BulkSyncRoutinesRequest(
    @NotNull(message = "Routine IDs list is required (can be empty to remove all)")
    List<UUID> routineIds,
    Instant expirationDate
) {
}

