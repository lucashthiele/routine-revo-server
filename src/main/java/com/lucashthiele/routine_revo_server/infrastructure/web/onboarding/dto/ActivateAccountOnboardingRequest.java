package com.lucashthiele.routine_revo_server.infrastructure.web.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActivateAccountOnboardingRequest(
    @NotBlank(message = "Password must not be empty")
    @Size(min = 8, max= 72, message="Password must have at least 8 characters and less than 72")
    String password) {
}
