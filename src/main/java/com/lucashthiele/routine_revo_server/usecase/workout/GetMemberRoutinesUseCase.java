package com.lucashthiele.routine_revo_server.usecase.workout;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.workout.input.GetMemberRoutinesInput;
import com.lucashthiele.routine_revo_server.usecase.workout.output.MemberRoutineItemOutput;
import com.lucashthiele.routine_revo_server.usecase.workout.output.MemberRoutineOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetMemberRoutinesUseCase implements UseCaseInterface<List<MemberRoutineOutput>, GetMemberRoutinesInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetMemberRoutinesUseCase.class);
  
  private final RoutineGateway routineGateway;
  
  public GetMemberRoutinesUseCase(RoutineGateway routineGateway) {
    this.routineGateway = routineGateway;
  }
  
  @Override
  public List<MemberRoutineOutput> execute(GetMemberRoutinesInput input) {
    LOGGER.info("[GetMemberRoutinesUseCase] Fetching routines for member: {}", input.memberId());
    
    List<Routine> routines = routineGateway.findAllByMemberId(input.memberId());
    
    List<MemberRoutineOutput> outputs = routines.stream()
        .map(this::toOutput)
        .collect(Collectors.toList());
    
    LOGGER.info("[GetMemberRoutinesUseCase] Found {} routines for member: {}", outputs.size(), input.memberId());
    
    return outputs;
  }
  
  private MemberRoutineOutput toOutput(Routine routine) {
    // Calculate isExpired: true if expirationDate is before now
    boolean isExpired = routine.getExpirationDate() != null 
        && routine.getExpirationDate().isBefore(Instant.now());
    
    List<MemberRoutineItemOutput> items = routine.getItems() != null
        ? routine.getItems().stream()
            .map(item -> new MemberRoutineItemOutput(
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
    
    return new MemberRoutineOutput(
        routine.getId(),
        routine.getName(),
        routine.getDescription(),
        routine.getExpirationDate(),
        isExpired,
        routine.getCreatorId(),
        items,
        routine.getCreatedAt(),
        routine.getUpdatedAt()
    );
  }
}

