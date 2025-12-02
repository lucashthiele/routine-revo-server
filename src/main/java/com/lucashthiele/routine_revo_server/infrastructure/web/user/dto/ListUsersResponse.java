package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

import java.util.List;

public record ListUsersResponse(
    List<UserResponse> users,
    long total,
    int page,
    int size,
    int totalPages
) {
}

