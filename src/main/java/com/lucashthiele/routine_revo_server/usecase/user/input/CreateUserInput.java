package com.lucashthiele.routine_revo_server.usecase.user.input;

import com.lucashthiele.routine_revo_server.domain.user.Role;

import java.util.UUID;

public record CreateUserInput(String name, String email, Role role, UUID coachId) {
}
