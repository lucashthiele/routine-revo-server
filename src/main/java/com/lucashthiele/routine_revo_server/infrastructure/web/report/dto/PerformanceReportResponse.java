package com.lucashthiele.routine_revo_server.infrastructure.web.report.dto;

import java.util.List;
import java.util.UUID;

public record PerformanceReportResponse(
    UUID memberId,
    String memberName,
    String memberEmail,
    Double adherenceRate,
    List<WorkoutHistoryItemResponse> workoutHistory
) {
}

