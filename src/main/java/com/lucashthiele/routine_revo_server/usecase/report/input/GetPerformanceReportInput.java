package com.lucashthiele.routine_revo_server.usecase.report.input;

import java.util.UUID;

public record GetPerformanceReportInput(
    UUID memberId
) {
}

