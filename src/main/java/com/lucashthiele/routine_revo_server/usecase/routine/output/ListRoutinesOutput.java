package com.lucashthiele.routine_revo_server.usecase.routine.output;

import java.util.List;

public record ListRoutinesOutput(
    List<RoutineOutput> routines,
    long total,
    int page,
    int size,
    int totalPages
) {
}

