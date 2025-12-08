package com.lucashthiele.routine_revo_server.usecase.routine.input;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BulkSyncRoutinesInput(
    UUID memberId,
    List<UUID> routineIds,
    Instant expirationDate
) {
}

