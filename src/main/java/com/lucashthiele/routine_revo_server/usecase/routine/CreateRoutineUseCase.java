package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineItem;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineType;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.routine.input.CreateRoutineInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreateRoutineUseCase implements UseCaseInterface<UUID, CreateRoutineInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateRoutineUseCase.class);
  
  private final RoutineGateway routineGateway;
  
  public CreateRoutineUseCase(RoutineGateway routineGateway) {
    this.routineGateway = routineGateway;
  }
  
  @Override
  public UUID execute(CreateRoutineInput input) {
    LOGGER.info("[CreateRoutineUseCase] Creating routine: {}", input.name());
    
    // Convert input items to domain items
    List<RoutineItem> items = input.items() != null
        ? input.items().stream()
            .map(itemInput -> RoutineItem.builder()
                .exerciseId(itemInput.exerciseId())
                .sets(itemInput.sets())
                .reps(itemInput.reps())
                .load(itemInput.load())
                .restTime(itemInput.restTime())
                .sequenceOrder(itemInput.sequenceOrder())
                .build())
            .collect(Collectors.toList())
        : List.of();
    
    // Determine routine type: if memberId is null and no explicit type, default to TEMPLATE
    RoutineType routineType = input.routineType() != null 
        ? input.routineType() 
        : (input.memberId() == null ? RoutineType.TEMPLATE : RoutineType.CUSTOM);
    
    // Create routine entity
    Routine routine = Routine.builder()
        .name(input.name())
        .description(input.description())
        .expirationDate(input.expirationDate())
        .creatorId(input.creatorId())
        .memberId(input.memberId())
        .routineType(routineType)
        .items(items)
        .build();
    
    // Save to database
    UUID routineId = routineGateway.save(routine);
    
    LOGGER.info("[CreateRoutineUseCase] Routine created successfully with ID: {}", routineId);
    
    return routineId;
  }
}

