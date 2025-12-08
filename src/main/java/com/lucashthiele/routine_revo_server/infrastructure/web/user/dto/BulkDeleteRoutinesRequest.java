package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record BulkDeleteRoutinesRequest(
    @NotEmpty(message = "At least one routine ID is required")
    List<UUID> routineIds
) {
}

