package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
    @NotBlank(message = "Name cannot be blank")
    String name,
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    String email
) {
}

