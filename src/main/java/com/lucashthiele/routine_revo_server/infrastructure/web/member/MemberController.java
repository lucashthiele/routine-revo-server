package com.lucashthiele.routine_revo_server.infrastructure.web.member;

import com.lucashthiele.routine_revo_server.infrastructure.security.SecurityContextHelper;
import com.lucashthiele.routine_revo_server.infrastructure.web.member.dto.*;
import com.lucashthiele.routine_revo_server.usecase.workout.GetMemberRoutinesUseCase;
import com.lucashthiele.routine_revo_server.usecase.workout.LogWorkoutUseCase;
import com.lucashthiele.routine_revo_server.usecase.workout.input.GetMemberRoutinesInput;
import com.lucashthiele.routine_revo_server.usecase.workout.input.LogWorkoutInput;
import com.lucashthiele.routine_revo_server.usecase.workout.input.LogWorkoutItemInput;
import com.lucashthiele.routine_revo_server.usecase.workout.output.LogWorkoutOutput;
import com.lucashthiele.routine_revo_server.usecase.workout.output.MemberRoutineOutput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/member")
public class MemberController {
  private static final Logger LOGGER = LoggerFactory.getLogger(MemberController.class);
  
  private final GetMemberRoutinesUseCase getMemberRoutinesUseCase;
  private final LogWorkoutUseCase logWorkoutUseCase;
  private final SecurityContextHelper securityContextHelper;
  
  public MemberController(
      GetMemberRoutinesUseCase getMemberRoutinesUseCase,
      LogWorkoutUseCase logWorkoutUseCase,
      SecurityContextHelper securityContextHelper) {
    this.getMemberRoutinesUseCase = getMemberRoutinesUseCase;
    this.logWorkoutUseCase = logWorkoutUseCase;
    this.securityContextHelper = securityContextHelper;
  }
  
  @GetMapping("/routines")
  public ResponseEntity<List<MemberRoutineResponse>> getMyRoutines() {
    UUID memberId = securityContextHelper.getCurrentUserId();
    LOGGER.info("GET /api/v1/member/routines - Fetching routines for member: {}", memberId);
    
    GetMemberRoutinesInput input = new GetMemberRoutinesInput(memberId);
    List<MemberRoutineOutput> outputs = getMemberRoutinesUseCase.execute(input);
    
    List<MemberRoutineResponse> responses = outputs.stream()
        .map(this::toRoutineResponse)
        .collect(Collectors.toList());
    
    LOGGER.info("GET /api/v1/member/routines - Returning {} routines for member: {}", 
        responses.size(), memberId);
    
    return ResponseEntity.ok(responses);
  }
  
  @PostMapping("/workouts")
  public ResponseEntity<LogWorkoutResponse> logWorkout(@Valid @RequestBody LogWorkoutRequest request) {
    UUID memberId = securityContextHelper.getCurrentUserId();
    LOGGER.info("POST /api/v1/member/workouts - Logging workout for member: {}", memberId);
    
    List<LogWorkoutItemInput> itemInputs = request.items() != null
        ? request.items().stream()
            .map(item -> new LogWorkoutItemInput(
                item.exerciseId(),
                item.setsDone(),
                item.repsDone(),
                item.loadUsed()
            ))
            .collect(Collectors.toList())
        : List.of();
    
    LogWorkoutInput input = new LogWorkoutInput(
        memberId,
        request.routineId(),
        request.startedAt(),
        request.endedAt(),
        itemInputs
    );
    
    LogWorkoutOutput output = logWorkoutUseCase.execute(input);
    
    LogWorkoutResponse response = new LogWorkoutResponse(
        output.workoutSessionId(),
        output.newAdherenceRate(),
        output.message()
    );
    
    LOGGER.info("POST /api/v1/member/workouts - Workout logged successfully for member: {}", memberId);
    
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
  
  private MemberRoutineResponse toRoutineResponse(MemberRoutineOutput output) {
    List<MemberRoutineItemResponse> items = output.items() != null
        ? output.items().stream()
            .map(item -> new MemberRoutineItemResponse(
                item.id(),
                item.exerciseId(),
                item.exerciseName(),
                item.exerciseImageUrl(),
                item.sets(),
                item.reps(),
                item.load(),
                item.restTime(),
                item.sequenceOrder()
            ))
            .collect(Collectors.toList())
        : List.of();
    
    return new MemberRoutineResponse(
        output.id(),
        output.name(),
        output.description(),
        output.expirationDate(),
        output.isExpired(),
        output.creatorId(),
        items,
        output.createdAt(),
        output.updatedAt()
    );
  }
}

