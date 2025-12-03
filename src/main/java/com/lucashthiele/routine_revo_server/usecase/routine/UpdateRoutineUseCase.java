package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineItem;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.routine.exception.RoutineNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.routine.input.UpdateRoutineInput;
import com.lucashthiele.routine_revo_server.usecase.routine.output.RoutineItemOutput;
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
    
    // Build updated routine
    Routine updatedRoutine = Routine.builder()
        .id(input.id())
        .name(input.name())
        .description(input.description())
        .expirationDate(input.expirationDate())
        .creatorId(existingRoutine.getCreatorId()) // Keep original creator
        .memberId(input.memberId())
        .items(items)
        .createdAt(existingRoutine.getCreatedAt())
        .build();
    
    // Update in database
    Routine savedRoutine = routineGateway.update(updatedRoutine);
    
    LOGGER.info("[UpdateRoutineUseCase] Routine updated successfully with ID: {}", savedRoutine.getId());
    
    // Convert to output
    return toOutput(savedRoutine);
  }
  
  private RoutineOutput toOutput(Routine routine) {
    List<RoutineItemOutput> itemOutputs = routine.getItems() != null
        ? routine.getItems().stream()
            .map(item -> new RoutineItemOutput(
                item.getId(),
                item.getExerciseId(),
                item.getExerciseName(),
                item.getExerciseImageUrl(),
                item.getSets(),
                item.getReps(),
                item.getLoad(),
                item.getRestTime(),
                item.getSequenceOrder()
            ))
            .collect(Collectors.toList())
        : List.of();
    
    return new RoutineOutput(
        routine.getId(),
        routine.getName(),
        routine.getDescription(),
        routine.getExpirationDate(),
        routine.getCreatorId(),
        routine.getMemberId(),
        itemOutputs,
        routine.getCreatedAt(),
        routine.getUpdatedAt()
    );
  }
}

