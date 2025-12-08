package com.lucashthiele.routine_revo_server.usecase.routine.input;

import java.util.List;
import java.util.UUID;

public record BulkDeleteRoutinesInput(
    UUID memberId,
    List<UUID> routineIds
) {
}

