package com.lucashthiele.routine_revo_server.domain.routine;

import java.util.UUID;

public record RoutineFilter(
    UUID creatorId,
    UUID memberId,
    Boolean isExpired,
    RoutineType routineType
) {
}

