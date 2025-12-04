package com.lucashthiele.routine_revo_server.usecase.report.output;

import java.util.List;
import java.util.UUID;

public record PerformanceReportOutput(
    UUID memberId,
    String memberName,
    String memberEmail,
    Double adherenceRate,
    List<WorkoutHistoryItem> workoutHistory
) {
}

