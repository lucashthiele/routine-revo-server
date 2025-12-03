package com.lucashthiele.routine_revo_server.usecase.routine.input;

import java.util.UUID;

public record AssociateRoutineInput(
    UUID routineId,
    UUID memberId
) {
}

