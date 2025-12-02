package com.lucashthiele.routine_revo_server.usecase.user.output;

import java.util.List;

public record ListUsersOutput(
    List<UserOutput> users,
    long total,
    int page,
    int size,
    int totalPages
) {
}

