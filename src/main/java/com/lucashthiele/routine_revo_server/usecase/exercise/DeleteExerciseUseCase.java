package com.lucashthiele.routine_revo_server.usecase.exercise;

import com.lucashthiele.routine_revo_server.domain.exercise.Exercise;
import com.lucashthiele.routine_revo_server.gateway.ExerciseGateway;
import com.lucashthiele.routine_revo_server.gateway.StorageGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.exercise.exception.ExerciseInUseException;
import com.lucashthiele.routine_revo_server.usecase.exercise.exception.ExerciseNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.exercise.input.DeleteExerciseInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeleteExerciseUseCase implements UseCaseInterface<Void, DeleteExerciseInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteExerciseUseCase.class);
  
  private final ExerciseGateway exerciseGateway;
  private final StorageGateway storageGateway;
  
  public DeleteExerciseUseCase(ExerciseGateway exerciseGateway, StorageGateway storageGateway) {
    this.exerciseGateway = exerciseGateway;
    this.storageGateway = storageGateway;
  }
  
  @Override
  public Void execute(DeleteExerciseInput input) {
    LOGGER.info("[DeleteExerciseUseCase] Deleting exercise with ID: {}", input.id());
    
    // Fetch existing exercise to get image URL
    Exercise exercise = exerciseGateway.findById(input.id())
        .orElseThrow(() -> new ExerciseNotFoundException(input.id()));
    
    // Check if exercise is being used in any routine
    List<String> routineNames = exerciseGateway.findRoutineNamesUsingExercise(input.id());
    if (!routineNames.isEmpty()) {
      LOGGER.warn("[DeleteExerciseUseCase] Cannot delete exercise {} - used in routines: {}", 
          input.id(), routineNames);
      throw new ExerciseInUseException(input.id(), routineNames);
    }
    
    // Delete image from S3 if exists
    if (exercise.getImageUrl() != null) {
      try {
        storageGateway.deleteFile(exercise.getImageUrl());
        LOGGER.debug("Exercise image deleted: {}", exercise.getImageUrl());
      } catch (Exception e) {
        LOGGER.warn("Failed to delete exercise image: {}", exercise.getImageUrl(), e);
      }
    }
    
    // Delete exercise from database
    exerciseGateway.delete(input.id());
    
    LOGGER.info("[DeleteExerciseUseCase] Exercise deleted successfully with ID: {}", input.id());
    
    return null;
  }
}

