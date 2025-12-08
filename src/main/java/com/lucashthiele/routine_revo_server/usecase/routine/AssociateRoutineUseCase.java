package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineItem;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineType;
import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.routine.exception.RoutineNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.routine.input.AssociateRoutineInput;
import com.lucashthiele.routine_revo_server.usecase.user.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssociateRoutineUseCase implements UseCaseInterface<UUID, AssociateRoutineInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AssociateRoutineUseCase.class);
  
  private final RoutineGateway routineGateway;
  private final UserGateway userGateway;
  
  public AssociateRoutineUseCase(RoutineGateway routineGateway, UserGateway userGateway) {
    this.routineGateway = routineGateway;
    this.userGateway = userGateway;
  }
  
  @Override
  public UUID execute(AssociateRoutineInput input) {
    LOGGER.info("[AssociateRoutineUseCase] Associating routine {} to member {}", 
        input.routineId(), input.memberId());
    
    // Verify routine exists
    Routine sourceRoutine = routineGateway.findById(input.routineId())
        .orElseThrow(() -> new RoutineNotFoundException(input.routineId()));
    
    // Verify user exists and is a MEMBER
    User member = userGateway.findById(input.memberId())
        .orElseThrow(() -> new UserNotFoundException(input.memberId()));
    
    if (member.getRole() != Role.MEMBER) {
      throw new IllegalArgumentException("User with ID " + input.memberId() + " is not a MEMBER");
    }
    
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
    
    // Determine template reference
    UUID templateId = sourceRoutine.getRoutineType() == RoutineType.TEMPLATE 
        ? sourceRoutine.getId() 
        : sourceRoutine.getTemplateId();
    
    // Create a NEW routine (copy) assigned to the member
    Routine newRoutine = Routine.builder()
        .name(sourceRoutine.getName())
        .description(sourceRoutine.getDescription())
        .expirationDate(sourceRoutine.getExpirationDate())
        .creatorId(sourceRoutine.getCreatorId())
        .memberId(input.memberId())
        .routineType(RoutineType.CUSTOM)
        .templateId(templateId)
        .items(copiedItems)
        .build();
    
    UUID newRoutineId = routineGateway.save(newRoutine);
    
    LOGGER.info("[AssociateRoutineUseCase] Created routine copy {} from {} for member {}", 
        newRoutineId, input.routineId(), input.memberId());
    
    return newRoutineId;
  }
}

