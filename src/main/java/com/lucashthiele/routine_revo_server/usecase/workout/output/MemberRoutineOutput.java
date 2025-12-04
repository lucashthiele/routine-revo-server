package com.lucashthiele.routine_revo_server.usecase.workout.output;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MemberRoutineOutput(
    UUID id,
    String name,
    String description,
    Instant expirationDate,
    boolean isExpired,
    UUID creatorId,
    List<MemberRoutineItemOutput> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

