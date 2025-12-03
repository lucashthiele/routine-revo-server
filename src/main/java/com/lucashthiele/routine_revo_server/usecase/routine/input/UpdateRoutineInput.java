package com.lucashthiele.routine_revo_server.usecase.routine.input;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UpdateRoutineInput(
    UUID id,
    String name,
    String description,
    Instant expirationDate,
    UUID memberId,
    List<RoutineItemInput> items
) {
}

