package com.lucashthiele.routine_revo_server.infrastructure.web.routine.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssociateRoutineRequest(
    @NotNull(message = "Member ID is required")
    UUID memberId
) {
}

