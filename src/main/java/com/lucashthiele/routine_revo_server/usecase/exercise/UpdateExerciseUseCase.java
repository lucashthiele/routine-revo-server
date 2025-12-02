package com.lucashthiele.routine_revo_server.usecase.exercise;

import com.lucashthiele.routine_revo_server.domain.exercise.Exercise;
import com.lucashthiele.routine_revo_server.gateway.ExerciseGateway;
import com.lucashthiele.routine_revo_server.gateway.StorageGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.exercise.exception.ExerciseNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.exercise.input.UpdateExerciseInput;
import com.lucashthiele.routine_revo_server.usecase.exercise.output.ExerciseOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateExerciseUseCase implements UseCaseInterface<ExerciseOutput, UpdateExerciseInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateExerciseUseCase.class);
  
  private final ExerciseGateway exerciseGateway;
  private final StorageGateway storageGateway;
  
  public UpdateExerciseUseCase(ExerciseGateway exerciseGateway, StorageGateway storageGateway) {
    this.exerciseGateway = exerciseGateway;
    this.storageGateway = storageGateway;
  }
  
  @Override
  public ExerciseOutput execute(UpdateExerciseInput input) {
    LOGGER.info("[UpdateExerciseUseCase] Updating exercise with ID: {}", input.id());
    
    // Fetch existing exercise
    Exercise existingExercise = exerciseGateway.findById(input.id())
        .orElseThrow(() -> new ExerciseNotFoundException(input.id()));
    
    String imageUrl = existingExercise.getImageUrl();
    
    // If new image is provided, upload it and delete the old one
    if (input.imageContent() != null && input.imageContentLength() != null) {
      LOGGER.debug("Uploading new exercise image: {}", input.imageFileName());
      
      // Delete old image if exists
      if (existingExercise.getImageUrl() != null) {
        try {
          storageGateway.deleteFile(existingExercise.getImageUrl());
          LOGGER.debug("Old image deleted: {}", existingExercise.getImageUrl());
        } catch (Exception e) {
          LOGGER.warn("Failed to delete old image: {}", existingExercise.getImageUrl(), e);
        }
      }
      
      // Upload new image
      imageUrl = storageGateway.uploadFile(
          input.imageFileName(),
          input.imageContent(),
          input.imageContentType(),
          input.imageContentLength()
      );
      LOGGER.debug("New image uploaded successfully: {}", imageUrl);
    }
    
    // Build updated exercise
    Exercise updatedExercise = Exercise.builder()
        .id(input.id())
        .name(input.name())
        .muscleGroup(input.muscleGroup())
        .description(input.description())
        .equipment(input.equipment())
        .imageUrl(imageUrl)
        .createdAt(existingExercise.getCreatedAt())
        .build();
    
    // Update in database
    Exercise savedExercise = exerciseGateway.update(updatedExercise);
    
    LOGGER.info("[UpdateExerciseUseCase] Exercise updated successfully with ID: {}", savedExercise.getId());
    
    return new ExerciseOutput(
        savedExercise.getId(),
        savedExercise.getName(),
        savedExercise.getMuscleGroup(),
        savedExercise.getDescription(),
        savedExercise.getEquipment(),
        savedExercise.getImageUrl(),
        savedExercise.getCreatedAt(),
        savedExercise.getUpdatedAt()
    );
  }
}

