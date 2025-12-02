package com.lucashthiele.routine_revo_server.usecase.exercise;

import com.lucashthiele.routine_revo_server.domain.exercise.ExerciseFilter;
import com.lucashthiele.routine_revo_server.domain.shared.PaginatedResult;
import com.lucashthiele.routine_revo_server.gateway.ExerciseGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.exercise.input.SearchExercisesInput;
import com.lucashthiele.routine_revo_server.usecase.exercise.output.ExerciseOutput;
import com.lucashthiele.routine_revo_server.usecase.exercise.output.SearchExercisesOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchExercisesUseCase implements UseCaseInterface<SearchExercisesOutput, SearchExercisesInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SearchExercisesUseCase.class);
  
  private final ExerciseGateway exerciseGateway;
  
  public SearchExercisesUseCase(ExerciseGateway exerciseGateway) {
    this.exerciseGateway = exerciseGateway;
  }
  
  @Override
  public SearchExercisesOutput execute(SearchExercisesInput input) {
    LOGGER.info("[SearchExercisesUseCase] Searching exercises with filter: name={}, muscleGroup={}, page={}, size={}",
        input.name(), input.muscleGroup(), input.page(), input.size());
    
    ExerciseFilter filter = new ExerciseFilter(input.name(), input.muscleGroup());
    
    PaginatedResult<com.lucashthiele.routine_revo_server.domain.exercise.Exercise> result = 
        exerciseGateway.findAll(filter, input.page(), input.size());
    
    List<ExerciseOutput> exercises = result.items().stream()
        .map(exercise -> new ExerciseOutput(
            exercise.getId(),
            exercise.getName(),
            exercise.getMuscleGroup(),
            exercise.getDescription(),
            exercise.getEquipment(),
            exercise.getImageUrl(),
            exercise.getCreatedAt(),
            exercise.getUpdatedAt()
        ))
        .toList();
    
    LOGGER.info("[SearchExercisesUseCase] Found {} exercises out of {} total", 
        exercises.size(), result.total());
    
    return new SearchExercisesOutput(
        exercises,
        result.total(),
        result.page(),
        result.size(),
        result.totalPages()
    );
  }
}

