package com.lucashthiele.routine_revo_server.infrastructure.web.member.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MemberRoutineResponse(
    UUID id,
    String name,
    String description,
    Instant expirationDate,
    boolean isExpired,
    UUID creatorId,
    List<MemberRoutineItemResponse> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}

