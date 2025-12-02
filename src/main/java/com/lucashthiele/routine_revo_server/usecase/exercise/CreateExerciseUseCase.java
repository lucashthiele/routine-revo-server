package com.lucashthiele.routine_revo_server.usecase.exercise;

import com.lucashthiele.routine_revo_server.domain.exercise.Exercise;
import com.lucashthiele.routine_revo_server.gateway.ExerciseGateway;
import com.lucashthiele.routine_revo_server.gateway.StorageGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.exercise.input.CreateExerciseInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateExerciseUseCase implements UseCaseInterface<UUID, CreateExerciseInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateExerciseUseCase.class);
  
  private final ExerciseGateway exerciseGateway;
  private final StorageGateway storageGateway;
  
  public CreateExerciseUseCase(ExerciseGateway exerciseGateway, StorageGateway storageGateway) {
    this.exerciseGateway = exerciseGateway;
    this.storageGateway = storageGateway;
  }
  
  @Override
  public UUID execute(CreateExerciseInput input) {
    LOGGER.info("[CreateExerciseUseCase] Creating exercise. Input: {}", input.name());
    
    // Upload image to S3
    String imageUrl = null;
    if (input.imageContent() != null) {
      LOGGER.debug("Uploading exercise image: {}", input.imageFileName());
      imageUrl = storageGateway.uploadFile(
          input.imageFileName(),
          input.imageContent(),
          input.imageContentType(),
          input.imageContentLength()
      );
      LOGGER.debug("Image uploaded successfully: {}", imageUrl);
    }
    
    // Create exercise entity
    Exercise exercise = Exercise.builder()
        .name(input.name())
        .muscleGroup(input.muscleGroup())
        .description(input.description())
        .equipment(input.equipment())
        .imageUrl(imageUrl)
        .build();
    
    // Save to database
    UUID exerciseId = exerciseGateway.create(exercise);
    
    LOGGER.info("[CreateExerciseUseCase] Exercise created successfully with ID: {}", exerciseId);
    
    return exerciseId;
  }
}

