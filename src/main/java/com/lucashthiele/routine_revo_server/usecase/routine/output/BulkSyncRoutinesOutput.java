package com.lucashthiele.routine_revo_server.usecase.routine.output;

public record BulkSyncRoutinesOutput(
    int addedCount,
    int removedCount,
    int unchangedCount,
    String message
) {
}

