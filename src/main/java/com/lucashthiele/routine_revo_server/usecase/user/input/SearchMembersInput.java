package com.lucashthiele.routine_revo_server.usecase.user.input;

public record SearchMembersInput(
    String name,
    Integer page,
    Integer size
) {
}

