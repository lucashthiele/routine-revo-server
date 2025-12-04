package com.lucashthiele.routine_revo_server.usecase.profile.input;

import java.util.UUID;

public record UpdateMyProfileInput(
    UUID userId,
    String name
) {
}

