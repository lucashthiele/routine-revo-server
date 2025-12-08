package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineItem;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineType;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.routine.exception.RoutineNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.routine.input.BulkSyncRoutinesInput;
import com.lucashthiele.routine_revo_server.usecase.routine.output.BulkSyncRoutinesOutput;
import com.lucashthiele.routine_revo_server.usecase.user.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BulkSyncRoutinesUseCase implements UseCaseInterface<BulkSyncRoutinesOutput, BulkSyncRoutinesInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(BulkSyncRoutinesUseCase.class);
  
  private final RoutineGateway routineGateway;
  private final UserGateway userGateway;
  
  public BulkSyncRoutinesUseCase(RoutineGateway routineGateway, UserGateway userGateway) {
    this.routineGateway = routineGateway;
    this.userGateway = userGateway;
  }
  
  @Override
  @Transactional
  public BulkSyncRoutinesOutput execute(BulkSyncRoutinesInput input) {
    LOGGER.info("[BulkSyncRoutinesUseCase] Syncing routines for member: {}, incoming routine count: {}", 
        input.memberId(), input.routineIds().size());
    
    // Verify member exists
    User member = userGateway.findById(input.memberId())
        .orElseThrow(() -> new UserNotFoundException(input.memberId()));
    
    // Fetch all existing routines for this member
    List<Routine> existingRoutines = routineGateway.findAllByMemberId(input.memberId());
    
    // Build a map of templateId -> existing routine (for routines that were assigned from templates)
    Map<UUID, Routine> existingByTemplateId = existingRoutines.stream()
        .filter(r -> r.getTemplateId() != null)
        .collect(Collectors.toMap(Routine::getTemplateId, r -> r, (r1, r2) -> r1));
    
    // Build set of incoming template IDs (we need to resolve each routineId to its templateId)
    Set<UUID> incomingTemplateIds = new HashSet<>();
    Map<UUID, Routine> sourceRoutinesCache = new HashMap<>();
    
    for (UUID routineId : input.routineIds()) {
      Routine sourceRoutine = routineGateway.findById(routineId)
          .orElseThrow(() -> new RoutineNotFoundException(routineId));
      sourceRoutinesCache.put(routineId, sourceRoutine);
      
      // Determine the template ID: if it's a TEMPLATE, use its ID; otherwise use its templateId
      UUID templateId = sourceRoutine.getRoutineType() == RoutineType.TEMPLATE 
          ? sourceRoutine.getId() 
          : sourceRoutine.getTemplateId();
      
      if (templateId != null) {
        incomingTemplateIds.add(templateId);
      }
    }
    
    int addedCount = 0;
    int removedCount = 0;
    int unchangedCount = 0;
    
    // Identify routines to ADD (incoming templateIds not in existing)
    for (UUID routineId : input.routineIds()) {
      Routine sourceRoutine = sourceRoutinesCache.get(routineId);
      
      UUID templateId = sourceRoutine.getRoutineType() == RoutineType.TEMPLATE 
          ? sourceRoutine.getId() 
          : sourceRoutine.getTemplateId();
      
      if (templateId != null && existingByTemplateId.containsKey(templateId)) {
        // Already exists - skip
        unchangedCount++;
        LOGGER.debug("[BulkSyncRoutinesUseCase] Routine from template {} already assigned to member {}, skipping", 
            templateId, input.memberId());
        continue;
      }
      
      // Create new routine copy
      Instant expirationDate = input.expirationDate() != null 
          ? input.expirationDate() 
          : sourceRoutine.getExpirationDate();
      
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
      addedCount++;
      
      LOGGER.debug("[BulkSyncRoutinesUseCase] Created routine {} from template {} for member {}", 
          newRoutineId, templateId, input.memberId());
    }
    
    // Identify routines to REMOVE (existing templateIds not in incoming)
    for (Map.Entry<UUID, Routine> entry : existingByTemplateId.entrySet()) {
      UUID templateId = entry.getKey();
      Routine existingRoutine = entry.getValue();
      
      if (!incomingTemplateIds.contains(templateId)) {
        routineGateway.delete(existingRoutine.getId());
        removedCount++;
        
        LOGGER.debug("[BulkSyncRoutinesUseCase] Removed routine {} (template {}) from member {}", 
            existingRoutine.getId(), templateId, input.memberId());
      }
    }
    
    String message = String.format("Sync complete: %d added, %d removed, %d unchanged", 
        addedCount, removedCount, unchangedCount);
    
    LOGGER.info("[BulkSyncRoutinesUseCase] {}", message);
    
    return new BulkSyncRoutinesOutput(addedCount, removedCount, unchangedCount, message);
  }
}

