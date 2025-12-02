package com.lucashthiele.routine_revo_server.infrastructure.data.exercise;

import com.lucashthiele.routine_revo_server.domain.exercise.Exercise;
import com.lucashthiele.routine_revo_server.domain.exercise.MuscleGroup;
import com.lucashthiele.routine_revo_server.infrastructure.data.exercise.enums.MuscleGroupData;
import org.springframework.stereotype.Component;

@Component
public class ExerciseDataMapper {
  
  public Exercise toDomain(ExerciseData data) {
    return Exercise.builder()
        .id(data.getId())
        .name(data.getName())
        .muscleGroup(toDomainMuscleGroup(data.getMuscleGroup()))
        .description(data.getDescription())
        .equipment(data.getEquipment())
        .imageUrl(data.getImageUrl())
        .createdAt(data.getCreatedAt())
        .updatedAt(data.getUpdatedAt())
        .build();
  }
  
  public ExerciseData toData(Exercise exercise) {
    ExerciseData data = new ExerciseData();
    data.setId(exercise.getId());
    data.setName(exercise.getName());
    data.setMuscleGroup(toDataMuscleGroup(exercise.getMuscleGroup()));
    data.setDescription(exercise.getDescription());
    data.setEquipment(exercise.getEquipment());
    data.setImageUrl(exercise.getImageUrl());
    
    return data;
  }
  
  private MuscleGroup toDomainMuscleGroup(MuscleGroupData data) {
    return MuscleGroup.valueOf(data.name());
  }
  
  private MuscleGroupData toDataMuscleGroup(MuscleGroup domain) {
    return MuscleGroupData.valueOf(domain.name());
  }
}

