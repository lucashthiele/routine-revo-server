package com.lucashthiele.routine_revo_server.gateway;

import com.lucashthiele.routine_revo_server.domain.exercise.Exercise;
import com.lucashthiele.routine_revo_server.domain.exercise.ExerciseFilter;
import com.lucashthiele.routine_revo_server.domain.shared.PaginatedResult;

import java.util.Optional;
import java.util.UUID;

public interface ExerciseGateway {
  UUID create(Exercise exercise);
  
  Optional<Exercise> findById(UUID id);
  
  PaginatedResult<Exercise> findAll(ExerciseFilter filter, int page, int size);
  
  Exercise update(Exercise exercise);
  
  void delete(UUID id);
}

