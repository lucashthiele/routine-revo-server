package com.lucashthiele.routine_revo_server.infrastructure.web.routine;

import com.lucashthiele.routine_revo_server.infrastructure.web.routine.dto.*;
import com.lucashthiele.routine_revo_server.usecase.routine.*;
import com.lucashthiele.routine_revo_server.usecase.routine.input.*;
import com.lucashthiele.routine_revo_server.usecase.routine.output.RoutineOutput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/routines")
public class RoutineController {
  private static final Logger LOGGER = LoggerFactory.getLogger(RoutineController.class);
  
  private final CreateRoutineUseCase createRoutineUseCase;
  private final UpdateRoutineUseCase updateRoutineUseCase;
  private final GetRoutineUseCase getRoutineUseCase;
  private final DeleteRoutineUseCase deleteRoutineUseCase;
  private final AssociateRoutineUseCase associateRoutineUseCase;
  
  public RoutineController(
      CreateRoutineUseCase createRoutineUseCase,
      UpdateRoutineUseCase updateRoutineUseCase,
      GetRoutineUseCase getRoutineUseCase,
      DeleteRoutineUseCase deleteRoutineUseCase,
      AssociateRoutineUseCase associateRoutineUseCase) {
    this.createRoutineUseCase = createRoutineUseCase;
    this.updateRoutineUseCase = updateRoutineUseCase;
    this.getRoutineUseCase = getRoutineUseCase;
    this.deleteRoutineUseCase = deleteRoutineUseCase;
    this.associateRoutineUseCase = associateRoutineUseCase;
  }
  
  @PostMapping
  public ResponseEntity<CreateRoutineResponse> createRoutine(@Valid @RequestBody CreateRoutineRequest request) {
    LOGGER.info("POST /api/v1/routines - Create routine request received");
    
    CreateRoutineInput input = new CreateRoutineInput(
        request.name(),
        request.description(),
        request.expirationDate(),
        request.creatorId(),
        request.memberId(),
        request.items() != null
            ? request.items().stream()
                .map(item -> new RoutineItemInput(
                    item.exerciseId(),
                    item.sets(),
                    item.reps(),
                    item.load(),
                    item.restTime(),
                    item.sequenceOrder()
                ))
                .collect(Collectors.toList())
            : null
    );
    
    UUID routineId = createRoutineUseCase.execute(input);
    
    LOGGER.info("POST /api/v1/routines - Routine created successfully with ID: {}", routineId);
    
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreateRoutineResponse(routineId, "Routine created successfully"));
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<RoutineResponse> getRoutine(@PathVariable UUID id) {
    LOGGER.info("GET /api/v1/routines/{} - Get routine request received", id);
    
    GetRoutineInput input = new GetRoutineInput(id);
    RoutineOutput output = getRoutineUseCase.execute(input);
    
    RoutineResponse response = toResponse(output);
    
    LOGGER.info("GET /api/v1/routines/{} - Routine found and returned", id);
    
    return ResponseEntity.ok(response);
  }
  
  @PutMapping("/{id}")
  public ResponseEntity<RoutineResponse> updateRoutine(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateRoutineRequest request) {
    LOGGER.info("PUT /api/v1/routines/{} - Update routine request received", id);
    
    UpdateRoutineInput input = new UpdateRoutineInput(
        id,
        request.name(),
        request.description(),
        request.expirationDate(),
        request.memberId(),
        request.items() != null
            ? request.items().stream()
                .map(item -> new RoutineItemInput(
                    item.exerciseId(),
                    item.sets(),
                    item.reps(),
                    item.load(),
                    item.restTime(),
                    item.sequenceOrder()
                ))
                .collect(Collectors.toList())
            : null
    );
    
    RoutineOutput output = updateRoutineUseCase.execute(input);
    RoutineResponse response = toResponse(output);
    
    LOGGER.info("PUT /api/v1/routines/{} - Routine updated successfully", id);
    
    return ResponseEntity.ok(response);
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRoutine(@PathVariable UUID id) {
    LOGGER.info("DELETE /api/v1/routines/{} - Delete routine request received", id);
    
    DeleteRoutineInput input = new DeleteRoutineInput(id);
    deleteRoutineUseCase.execute(input);
    
    LOGGER.info("DELETE /api/v1/routines/{} - Routine deleted successfully", id);
    
    return ResponseEntity.noContent().build();
  }
  
  @PostMapping("/{id}/associate")
  public ResponseEntity<Void> associateRoutine(
      @PathVariable UUID id,
      @Valid @RequestBody AssociateRoutineRequest request) {
    LOGGER.info("POST /api/v1/routines/{}/associate - Associate routine request received", id);
    
    AssociateRoutineInput input = new AssociateRoutineInput(id, request.memberId());
    associateRoutineUseCase.execute(input);
    
    LOGGER.info("POST /api/v1/routines/{}/associate - Routine associated successfully", id);
    
    return ResponseEntity.ok().build();
  }
  
  private RoutineResponse toResponse(RoutineOutput output) {
    return new RoutineResponse(
        output.id(),
        output.name(),
        output.description(),
        output.expirationDate(),
        output.creatorId(),
        output.memberId(),
        output.items() != null
            ? output.items().stream()
                .map(item -> new RoutineItemResponse(
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
            : null,
        output.createdAt(),
        output.updatedAt()
    );
  }
}

