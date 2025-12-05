package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.routine.exception.RoutineNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.routine.input.GetRoutineInput;
import com.lucashthiele.routine_revo_server.usecase.routine.output.RoutineOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetRoutineUseCase implements UseCaseInterface<RoutineOutput, GetRoutineInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetRoutineUseCase.class);
  
  private final RoutineGateway routineGateway;
  
  public GetRoutineUseCase(RoutineGateway routineGateway) {
    this.routineGateway = routineGateway;
  }
  
  @Override
  public RoutineOutput execute(GetRoutineInput input) {
    LOGGER.info("[GetRoutineUseCase] Fetching routine with ID: {}", input.id());
    
    Routine routine = routineGateway.findById(input.id())
        .orElseThrow(() -> new RoutineNotFoundException(input.id()));
    
    LOGGER.info("[GetRoutineUseCase] Routine found with ID: {}", input.id());
    
    return RoutineOutput.from(routine);
  }
}
