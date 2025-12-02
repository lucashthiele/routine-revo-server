package com.lucashthiele.routine_revo_server.usecase.exercise;

import com.lucashthiele.routine_revo_server.domain.exercise.Exercise;
import com.lucashthiele.routine_revo_server.gateway.ExerciseGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.exercise.exception.ExerciseNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.exercise.input.GetExerciseByIdInput;
import com.lucashthiele.routine_revo_server.usecase.exercise.output.ExerciseOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetExerciseByIdUseCase implements UseCaseInterface<ExerciseOutput, GetExerciseByIdInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetExerciseByIdUseCase.class);
  
  private final ExerciseGateway exerciseGateway;
  
  public GetExerciseByIdUseCase(ExerciseGateway exerciseGateway) {
    this.exerciseGateway = exerciseGateway;
  }
  
  @Override
  public ExerciseOutput execute(GetExerciseByIdInput input) {
    LOGGER.info("[GetExerciseByIdUseCase] Fetching exercise with ID: {}", input.id());
    
    Exercise exercise = exerciseGateway.findById(input.id())
        .orElseThrow(() -> new ExerciseNotFoundException(input.id()));
    
    LOGGER.info("[GetExerciseByIdUseCase] Exercise found with ID: {}", input.id());
    
    return new ExerciseOutput(
        exercise.getId(),
        exercise.getName(),
        exercise.getMuscleGroup(),
        exercise.getDescription(),
        exercise.getEquipment(),
        exercise.getImageUrl(),
        exercise.getCreatedAt(),
        exercise.getUpdatedAt()
    );
  }
}

