package com.lucashthiele.routine_revo_server.infrastructure.web.routine.dto;

import com.lucashthiele.routine_revo_server.domain.routine.RoutineType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoutineResponse(
    UUID id,
    String name,
    String description,
    Instant expirationDate,
    Boolean isExpired,
    UUID creatorId,
    UUID memberId,
    RoutineType routineType,
    UUID templateId,
    Integer itemCount,
    List<RoutineItemResponse> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
