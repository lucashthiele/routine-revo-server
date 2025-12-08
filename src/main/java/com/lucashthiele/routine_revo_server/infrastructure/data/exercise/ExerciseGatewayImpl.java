package com.lucashthiele.routine_revo_server.infrastructure.data.exercise;

import com.lucashthiele.routine_revo_server.domain.exercise.Exercise;
import com.lucashthiele.routine_revo_server.domain.exercise.ExerciseFilter;
import com.lucashthiele.routine_revo_server.domain.shared.PaginatedResult;
import com.lucashthiele.routine_revo_server.gateway.ExerciseGateway;
import com.lucashthiele.routine_revo_server.infrastructure.data.routine.RoutineData;
import com.lucashthiele.routine_revo_server.infrastructure.data.routine.RoutineJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ExerciseGatewayImpl implements ExerciseGateway {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseGatewayImpl.class);
  
  private final ExerciseJpaRepository exerciseRepository;
  private final ExerciseDataMapper exerciseDataMapper;
  private final RoutineJpaRepository routineRepository;
  
  public ExerciseGatewayImpl(
      ExerciseJpaRepository exerciseRepository, 
      ExerciseDataMapper exerciseDataMapper,
      RoutineJpaRepository routineRepository) {
    this.exerciseRepository = exerciseRepository;
    this.exerciseDataMapper = exerciseDataMapper;
    this.routineRepository = routineRepository;
  }
  
  @Override
  public UUID create(Exercise exercise) {
    ExerciseData exerciseData = exerciseDataMapper.toData(exercise);
    
    // Generate UUID if not already set
    if (exerciseData.getId() == null) {
      exerciseData.setId(UUID.randomUUID());
      LOGGER.debug("Generated new UUID for exercise: {}", exerciseData.getId());
    }
    
    LOGGER.info("Creating exercise with name: {}", exerciseData.getName());
    ExerciseData createdExercise = exerciseRepository.save(exerciseData);
    LOGGER.info("Exercise created successfully with ID: {}", createdExercise.getId());
    
    return createdExercise.getId();
  }
  
  @Override
  public Optional<Exercise> findById(UUID id) {
    LOGGER.debug("Fetching exercise from database by ID: {}", id);
    Optional<ExerciseData> exerciseDataOptional = exerciseRepository.findById(id);
    
    if (exerciseDataOptional.isPresent()) {
      LOGGER.debug("Exercise found in database with ID: {}", id);
    } else {
      LOGGER.debug("Exercise not found in database with ID: {}", id);
    }
    
    return exerciseDataOptional.map(exerciseDataMapper::toDomain);
  }
  
  @Override
  public PaginatedResult<Exercise> findAll(ExerciseFilter filter, int page, int size) {
    LOGGER.debug("Fetching exercises with filter: {}, page: {}, size: {}", filter, page, size);
    
    Specification<ExerciseData> spec = ExerciseSpecifications.withFilter(filter);
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    
    Page<ExerciseData> exerciseDataPage = exerciseRepository.findAll(spec, pageable);
    
    List<Exercise> exercises = exerciseDataPage.getContent().stream()
        .map(exerciseDataMapper::toDomain)
        .toList();
    
    LOGGER.debug("Found {} exercises out of {} total", exercises.size(), exerciseDataPage.getTotalElements());
    
    return PaginatedResult.of(exercises, exerciseDataPage.getTotalElements(), page, size);
  }
  
  @Override
  public Exercise update(Exercise exercise) {
    LOGGER.info("Updating exercise with ID: {}", exercise.getId());
    ExerciseData exerciseData = exerciseDataMapper.toData(exercise);
    ExerciseData updatedExerciseData = exerciseRepository.save(exerciseData);
    LOGGER.info("Exercise updated successfully with ID: {}", updatedExerciseData.getId());
    
    return exerciseDataMapper.toDomain(updatedExerciseData);
  }
  
  @Override
  public void delete(UUID id) {
    LOGGER.info("Deleting exercise with ID: {}", id);
    exerciseRepository.deleteById(id);
    LOGGER.info("Exercise deleted successfully with ID: {}", id);
  }
  
  @Override
  public List<String> findRoutineNamesUsingExercise(UUID exerciseId) {
    LOGGER.debug("Checking if exercise {} is used in any routine", exerciseId);
    List<RoutineData> routines = routineRepository.findAllByExerciseId(exerciseId);
    return routines.stream()
        .map(RoutineData::getName)
        .toList();
  }
}

