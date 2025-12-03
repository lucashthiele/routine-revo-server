package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.routine.exception.RoutineNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.routine.input.DeleteRoutineInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeleteRoutineUseCase implements UseCaseInterface<Void, DeleteRoutineInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteRoutineUseCase.class);
  
  private final RoutineGateway routineGateway;
  
  public DeleteRoutineUseCase(RoutineGateway routineGateway) {
    this.routineGateway = routineGateway;
  }
  
  @Override
  public Void execute(DeleteRoutineInput input) {
    LOGGER.info("[DeleteRoutineUseCase] Deleting routine with ID: {}", input.id());
    
    // Verify routine exists
    routineGateway.findById(input.id())
        .orElseThrow(() -> new RoutineNotFoundException(input.id()));
    
    // Delete routine
    routineGateway.delete(input.id());
    
    LOGGER.info("[DeleteRoutineUseCase] Routine deleted successfully with ID: {}", input.id());
    
    return null;
  }
}

