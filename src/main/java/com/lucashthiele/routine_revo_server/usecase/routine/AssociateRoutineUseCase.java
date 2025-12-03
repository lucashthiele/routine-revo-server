package com.lucashthiele.routine_revo_server.usecase.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
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

@Service
public class AssociateRoutineUseCase implements UseCaseInterface<Void, AssociateRoutineInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(AssociateRoutineUseCase.class);
  
  private final RoutineGateway routineGateway;
  private final UserGateway userGateway;
  
  public AssociateRoutineUseCase(RoutineGateway routineGateway, UserGateway userGateway) {
    this.routineGateway = routineGateway;
    this.userGateway = userGateway;
  }
  
  @Override
  public Void execute(AssociateRoutineInput input) {
    LOGGER.info("[AssociateRoutineUseCase] Associating routine {} to member {}", 
        input.routineId(), input.memberId());
    
    // Verify routine exists
    Routine routine = routineGateway.findById(input.routineId())
        .orElseThrow(() -> new RoutineNotFoundException(input.routineId()));
    
    // Verify user exists and is a MEMBER
    User member = userGateway.findById(input.memberId())
        .orElseThrow(() -> new UserNotFoundException(input.memberId()));
    
    if (member.getRole() != Role.MEMBER) {
      throw new IllegalArgumentException("User with ID " + input.memberId() + " is not a MEMBER");
    }
    
    // Update routine with member association
    Routine updatedRoutine = Routine.builder()
        .id(routine.getId())
        .name(routine.getName())
        .description(routine.getDescription())
        .expirationDate(routine.getExpirationDate())
        .creatorId(routine.getCreatorId())
        .memberId(input.memberId())
        .items(routine.getItems())
        .createdAt(routine.getCreatedAt())
        .build();
    
    routineGateway.update(updatedRoutine);
    
    LOGGER.info("[AssociateRoutineUseCase] Routine successfully associated to member");
    
    return null;
  }
}

