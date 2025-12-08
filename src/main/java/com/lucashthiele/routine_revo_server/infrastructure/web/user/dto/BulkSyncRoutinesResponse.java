package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

public record BulkSyncRoutinesResponse(
    int addedCount,
    int removedCount,
    int unchangedCount,
    String message
) {
}

