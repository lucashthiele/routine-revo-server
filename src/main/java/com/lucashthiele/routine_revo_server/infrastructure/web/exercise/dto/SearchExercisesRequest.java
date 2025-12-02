package com.lucashthiele.routine_revo_server.infrastructure.web.exercise.dto;

import com.lucashthiele.routine_revo_server.domain.exercise.MuscleGroup;

public record SearchExercisesRequest(
    String name,
    MuscleGroup muscleGroup,
    Integer page,
    Integer size
) {
  public int getPage() {
    return page != null ? page : 0;
  }
  
  public int getSize() {
    return size != null && size > 0 && size <= 100 ? size : 20;
  }
}

