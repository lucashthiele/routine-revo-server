package com.lucashthiele.routine_revo_server.usecase.routine.output;

public record BulkDeleteRoutinesOutput(
    int deletedCount,
    String message
) {
}

