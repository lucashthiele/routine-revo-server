package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LinkCoachRequest(
    @NotNull(message = "Coach ID cannot be null")
    UUID coachId
) {
}

