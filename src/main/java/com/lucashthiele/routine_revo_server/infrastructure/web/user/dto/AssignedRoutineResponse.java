package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

import java.time.Instant;
import java.util.UUID;

public record AssignedRoutineResponse(
    UUID id,
    String name,
    Instant expirationDate,
    Boolean isExpired
) {
}

