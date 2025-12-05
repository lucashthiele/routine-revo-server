package com.lucashthiele.routine_revo_server.infrastructure.web.routine.dto;

import java.util.List;

public record ListRoutinesResponse(
    List<RoutineResponse> routines,
    long total,
    int page,
    int size,
    int totalPages
) {
}

