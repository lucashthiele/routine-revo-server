package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineItem;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineType;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.routine.exception.RoutineNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.routine.input.BulkAssignRoutinesInput;
import com.lucashthiele.routine_revo_server.usecase.routine.output.BulkAssignRoutinesOutput;
import com.lucashthiele.routine_revo_server.usecase.user.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BulkAssignRoutinesUseCase implements UseCaseInterface<BulkAssignRoutinesOutput, BulkAssignRoutinesInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(BulkAssignRoutinesUseCase.class);
  
  private final RoutineGateway routineGateway;
  private final UserGateway userGateway;
  
  public BulkAssignRoutinesUseCase(RoutineGateway routineGateway, UserGateway userGateway) {
    this.routineGateway = routineGateway;
    this.userGateway = userGateway;
  }
  
  @Override
  @Transactional
  public BulkAssignRoutinesOutput execute(BulkAssignRoutinesInput input) {
    LOGGER.info("[BulkAssignRoutinesUseCase] Bulk assigning {} routines to member: {}", 
        input.routineIds().size(), input.memberId());
    
    // Verify member exists
    User member = userGateway.findById(input.memberId())
        .orElseThrow(() -> new UserNotFoundException(input.memberId()));
    
    int assignedCount = 0;
    
    for (UUID routineId : input.routineIds()) {
      Routine sourceRoutine = routineGateway.findById(routineId)
          .orElseThrow(() -> new RoutineNotFoundException(routineId));
      
      // Determine expiration date - use input if provided, otherwise keep existing
      Instant expirationDate = input.expirationDate() != null 
          ? input.expirationDate() 
          : sourceRoutine.getExpirationDate();
      
      // Copy routine items with new IDs
      List<RoutineItem> copiedItems = sourceRoutine.getItems() != null
          ? sourceRoutine.getItems().stream()
              .map(item -> RoutineItem.builder()
                  .exerciseId(item.getExerciseId())
                  .exerciseName(item.getExerciseName())
                  .exerciseImageUrl(item.getExerciseImageUrl())
                  .sets(item.getSets())
                  .reps(item.getReps())
                  .load(item.getLoad())
                  .restTime(item.getRestTime())
                  .sequenceOrder(item.getSequenceOrder())
                  .build())
              .collect(Collectors.toList())
          : List.of();
      
      // Determine template reference: if source is a TEMPLATE, reference it; 
      // otherwise inherit the source's template reference
      UUID templateId = sourceRoutine.getRoutineType() == RoutineType.TEMPLATE 
          ? sourceRoutine.getId() 
          : sourceRoutine.getTemplateId();
      
      // Create a NEW routine (copy) assigned to the member
      Routine newRoutine = Routine.builder()
          .name(sourceRoutine.getName())
          .description(sourceRoutine.getDescription())
          .expirationDate(expirationDate)
          .creatorId(sourceRoutine.getCreatorId())
          .memberId(member.getId())
          .routineType(RoutineType.CUSTOM)
          .templateId(templateId)
          .items(copiedItems)
          .build();
      
      UUID newRoutineId = routineGateway.save(newRoutine);
      assignedCount++;
      
      LOGGER.debug("[BulkAssignRoutinesUseCase] Created routine copy {} from {} for member {}", 
          newRoutineId, routineId, input.memberId());
    }
    
    String message = String.format("%d routine(s) assigned successfully to member", assignedCount);
    
    LOGGER.info("[BulkAssignRoutinesUseCase] {}", message);
    
    return new BulkAssignRoutinesOutput(assignedCount, message);
  }
}

