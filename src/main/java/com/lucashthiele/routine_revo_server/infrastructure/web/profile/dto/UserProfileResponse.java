package com.lucashthiele.routine_revo_server.infrastructure.web.profile.dto;

import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;

import java.util.UUID;

public record UserProfileResponse(
    UUID id,
    String name,
    String email,
    Role role,
    Status status,
    UUID coachId,
    Integer workoutPerWeek,
    Double adherenceRate
) {
}

