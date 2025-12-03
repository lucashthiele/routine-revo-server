package com.lucashthiele.routine_revo_server.usecase.routine.output;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoutineOutput(
    UUID id,
    String name,
    String description,
    Instant expirationDate,
    UUID creatorId,
    UUID memberId,
    List<RoutineItemOutput> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

