package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

public record BulkAssignRoutinesResponse(
    int assignedCount,
    String message
) {
}

