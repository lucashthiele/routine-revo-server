package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

public record BulkDeleteRoutinesResponse(
    int deletedCount,
    String message
) {
}

