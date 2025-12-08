package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineItem;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.routine.exception.RoutineNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.routine.input.UpdateRoutineInput;
import com.lucashthiele.routine_revo_server.usecase.routine.output.RoutineOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateRoutineUseCase implements UseCaseInterface<RoutineOutput, UpdateRoutineInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRoutineUseCase.class);
  
  private final RoutineGateway routineGateway;
  
  public UpdateRoutineUseCase(RoutineGateway routineGateway) {
    this.routineGateway = routineGateway;
  }
  
  @Override
  public RoutineOutput execute(UpdateRoutineInput input) {
    LOGGER.info("[UpdateRoutineUseCase] Updating routine with ID: {}", input.id());
    
    // Verify routine exists
    Routine existingRoutine = routineGateway.findById(input.id())
        .orElseThrow(() -> new RoutineNotFoundException(input.id()));
    
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
    
    // Build updated routine (preserve immutable fields: creatorId, routineType, templateId)
    Routine updatedRoutine = Routine.builder()
        .id(input.id())
        .name(input.name())
        .description(input.description())
        .expirationDate(input.expirationDate())
        .creatorId(existingRoutine.getCreatorId())
        .memberId(input.memberId())
        .routineType(existingRoutine.getRoutineType())
        .templateId(existingRoutine.getTemplateId())
        .items(items)
        .createdAt(existingRoutine.getCreatedAt())
        .build();
    
    // Update in database
    Routine savedRoutine = routineGateway.update(updatedRoutine);
    
    LOGGER.info("[UpdateRoutineUseCase] Routine updated successfully with ID: {}", savedRoutine.getId());
    
    return RoutineOutput.from(savedRoutine);
  }
}
