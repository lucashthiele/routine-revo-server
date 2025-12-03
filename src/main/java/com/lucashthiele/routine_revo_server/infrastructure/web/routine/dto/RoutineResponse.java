package com.lucashthiele.routine_revo_server.infrastructure.web.routine.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoutineResponse(
    UUID id,
    String name,
    String description,
    Instant expirationDate,
    UUID creatorId,
    UUID memberId,
    List<RoutineItemResponse> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

