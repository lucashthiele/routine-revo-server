package com.lucashthiele.routine_revo_server.infrastructure.web.routine.dto;

import java.util.UUID;

public record CreateRoutineResponse(
    UUID id,
    String message
) {
}

