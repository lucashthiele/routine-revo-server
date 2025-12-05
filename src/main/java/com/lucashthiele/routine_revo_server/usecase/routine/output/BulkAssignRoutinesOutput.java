package com.lucashthiele.routine_revo_server.usecase.routine.output;

public record BulkAssignRoutinesOutput(
    int assignedCount,
    String message
) {
}

