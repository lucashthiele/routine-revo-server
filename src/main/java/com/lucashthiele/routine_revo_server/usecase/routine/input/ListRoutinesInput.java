package com.lucashthiele.routine_revo_server.usecase.routine.input;

import java.util.UUID;

public record ListRoutinesInput(
    UUID creatorId,
    UUID memberId,
    Boolean isExpired,
    Boolean templatesOnly,
    int page,
    int size
) {
}

