package com.lucashthiele.routine_revo_server.usecase.routine.input;

import com.lucashthiele.routine_revo_server.domain.routine.RoutineType;

import java.util.UUID;

public record ListRoutinesInput(
    UUID creatorId,
    UUID memberId,
    Boolean isExpired,
    RoutineType routineType,
    int page,
    int size
) {
}

