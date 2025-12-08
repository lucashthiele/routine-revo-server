package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.routine.exception.RoutineNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.routine.input.BulkDeleteRoutinesInput;
import com.lucashthiele.routine_revo_server.usecase.routine.output.BulkDeleteRoutinesOutput;
import com.lucashthiele.routine_revo_server.usecase.user.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BulkDeleteRoutinesUseCase implements UseCaseInterface<BulkDeleteRoutinesOutput, BulkDeleteRoutinesInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(BulkDeleteRoutinesUseCase.class);
  
  private final RoutineGateway routineGateway;
  private final UserGateway userGateway;
  
  public BulkDeleteRoutinesUseCase(RoutineGateway routineGateway, UserGateway userGateway) {
    this.routineGateway = routineGateway;
    this.userGateway = userGateway;
  }
  
  @Override
  @Transactional
  public BulkDeleteRoutinesOutput execute(BulkDeleteRoutinesInput input) {
    LOGGER.info("[BulkDeleteRoutinesUseCase] Bulk deleting {} routines from member: {}", 
        input.routineIds().size(), input.memberId());
    
    // Verify member exists
    User member = userGateway.findById(input.memberId())
        .orElseThrow(() -> new UserNotFoundException(input.memberId()));
    
    int deletedCount = 0;
    
    for (UUID routineId : input.routineIds()) {
      Routine routine = routineGateway.findById(routineId)
          .orElseThrow(() -> new RoutineNotFoundException(routineId));
      
      // Verify routine belongs to the member
      if (routine.getMemberId() == null || !routine.getMemberId().equals(member.getId())) {
        LOGGER.warn("[BulkDeleteRoutinesUseCase] Routine {} does not belong to member {}, skipping", 
            routineId, input.memberId());
        continue;
      }
      
      routineGateway.delete(routineId);
      deletedCount++;
      
      LOGGER.debug("[BulkDeleteRoutinesUseCase] Deleted routine {} from member {}", 
          routineId, input.memberId());
    }
    
    String message = String.format("%d routine(s) deleted successfully from member", deletedCount);
    
    LOGGER.info("[BulkDeleteRoutinesUseCase] {}", message);
    
    return new BulkDeleteRoutinesOutput(deletedCount, message);
  }
}

