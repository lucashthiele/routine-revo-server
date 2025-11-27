package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

import com.lucashthiele.routine_revo_server.domain.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateUserRequest(
    @NotBlank
    String name,
    @NotBlank
    @Email
    String email,
    @NotBlank
    Role role,
    UUID coachId
) {
}
