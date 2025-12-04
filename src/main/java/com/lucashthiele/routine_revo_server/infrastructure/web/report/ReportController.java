package com.lucashthiele.routine_revo_server.infrastructure.web.report;

import com.lucashthiele.routine_revo_server.infrastructure.web.report.dto.PerformanceReportResponse;
import com.lucashthiele.routine_revo_server.infrastructure.web.report.dto.WorkoutHistoryItemResponse;
import com.lucashthiele.routine_revo_server.usecase.report.GetPerformanceReportUseCase;
import com.lucashthiele.routine_revo_server.usecase.report.input.GetPerformanceReportInput;
import com.lucashthiele.routine_revo_server.usecase.report.output.PerformanceReportOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);
  
  private final GetPerformanceReportUseCase getPerformanceReportUseCase;

  public ReportController(GetPerformanceReportUseCase getPerformanceReportUseCase) {
    this.getPerformanceReportUseCase = getPerformanceReportUseCase;
  }

  @GetMapping("/members/{memberId}")
  public ResponseEntity<PerformanceReportResponse> getPerformanceReport(@PathVariable UUID memberId) {
    LOGGER.info("GET /api/v1/reports/members/{} - Performance report request received", memberId);
    
    GetPerformanceReportInput input = new GetPerformanceReportInput(memberId);
    PerformanceReportOutput output = getPerformanceReportUseCase.execute(input);
    
    List<WorkoutHistoryItemResponse> workoutHistory = output.workoutHistory().stream()
        .map(item -> new WorkoutHistoryItemResponse(
            item.workoutSessionId(),
            item.date(),
            item.routineName(),
            item.durationMinutes()
        ))
        .collect(Collectors.toList());
    
    PerformanceReportResponse response = new PerformanceReportResponse(
        output.memberId(),
        output.memberName(),
        output.memberEmail(),
        output.adherenceRate(),
        workoutHistory
    );
    
    LOGGER.info("GET /api/v1/reports/members/{} - Performance report generated successfully", memberId);
    return ResponseEntity.ok(response);
  }
}

