package com.lucashthiele.routine_revo_server.usecase.passwordreset.input;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordInput(@NotBlank String token, @NotBlank @Min(8) @Max(72) String password) {
}
