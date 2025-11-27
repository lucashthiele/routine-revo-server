package com.lucashthiele.routine_revo_server.usecase.user.input;

import java.util.UUID;

public record LinkCoachInput(UUID coachId, UUID userId) {
}
