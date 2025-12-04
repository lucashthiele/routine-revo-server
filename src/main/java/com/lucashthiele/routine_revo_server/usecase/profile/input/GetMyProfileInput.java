package com.lucashthiele.routine_revo_server.usecase.profile.input;

import java.util.UUID;

public record GetMyProfileInput(
    UUID userId
) {
}

