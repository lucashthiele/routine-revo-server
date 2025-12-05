package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
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
import java.util.UUID;

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
      Routine routine = routineGateway.findById(routineId)
          .orElseThrow(() -> new RoutineNotFoundException(routineId));
      
      // Determine expiration date - use input if provided, otherwise keep existing
      Instant expirationDate = input.expirationDate() != null 
          ? input.expirationDate() 
          : routine.getExpirationDate();
      
      // Build updated routine with new member assignment
      Routine updatedRoutine = Routine.builder()
          .id(routine.getId())
          .name(routine.getName())
          .description(routine.getDescription())
          .expirationDate(expirationDate)
          .creatorId(routine.getCreatorId())
          .memberId(member.getId())
          .items(routine.getItems())
          .createdAt(routine.getCreatedAt())
          .build();
      
      routineGateway.update(updatedRoutine);
      assignedCount++;
      
      LOGGER.debug("[BulkAssignRoutinesUseCase] Assigned routine {} to member {}", routineId, input.memberId());
    }
    
    String message = String.format("%d routine(s) assigned successfully to member", assignedCount);
    
    LOGGER.info("[BulkAssignRoutinesUseCase] {}", message);
    
    return new BulkAssignRoutinesOutput(assignedCount, message);
  }
}

