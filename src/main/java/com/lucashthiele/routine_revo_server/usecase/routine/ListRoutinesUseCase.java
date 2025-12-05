package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineFilter;
import com.lucashthiele.routine_revo_server.domain.shared.PaginatedResult;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.routine.input.ListRoutinesInput;
import com.lucashthiele.routine_revo_server.usecase.routine.output.ListRoutinesOutput;
import com.lucashthiele.routine_revo_server.usecase.routine.output.RoutineOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListRoutinesUseCase implements UseCaseInterface<ListRoutinesOutput, ListRoutinesInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ListRoutinesUseCase.class);
  
  private final RoutineGateway routineGateway;
  
  public ListRoutinesUseCase(RoutineGateway routineGateway) {
    this.routineGateway = routineGateway;
  }
  
  @Override
  public ListRoutinesOutput execute(ListRoutinesInput input) {
    LOGGER.info("[ListRoutinesUseCase] Listing routines with creatorId: {}, memberId: {}, isExpired: {}, templatesOnly: {}, page: {}, size: {}",
        input.creatorId(), input.memberId(), input.isExpired(), input.templatesOnly(), input.page(), input.size());
    
    RoutineFilter filter = new RoutineFilter(
        input.creatorId(),
        input.memberId(),
        input.isExpired(),
        input.templatesOnly()
    );
    
    PaginatedResult<Routine> result = routineGateway.findAll(filter, input.page(), input.size());
    
    List<RoutineOutput> routineOutputs = result.items().stream()
        .map(RoutineOutput::from)
        .toList();
    
    LOGGER.info("[ListRoutinesUseCase] Found {} routines out of {} total", routineOutputs.size(), result.total());
    
    return new ListRoutinesOutput(
        routineOutputs,
        result.total(),
        result.page(),
        result.size(),
        result.totalPages()
    );
  }
}

