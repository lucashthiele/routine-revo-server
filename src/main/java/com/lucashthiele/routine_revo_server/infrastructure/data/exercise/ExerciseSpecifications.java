package com.lucashthiele.routine_revo_server.infrastructure.data.exercise;

import com.lucashthiele.routine_revo_server.domain.exercise.ExerciseFilter;
import com.lucashthiele.routine_revo_server.infrastructure.data.exercise.enums.MuscleGroupData;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSpecifications {
  
  public static Specification<ExerciseData> withFilter(ExerciseFilter filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      
      if (filter.name() != null && !filter.name().isBlank()) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + filter.name().toLowerCase() + "%"
            )
        );
      }
      
      if (filter.muscleGroup() != null) {
        MuscleGroupData muscleGroupData = MuscleGroupData.valueOf(filter.muscleGroup().name());
        predicates.add(
            criteriaBuilder.equal(root.get("muscleGroup"), muscleGroupData)
        );
      }
      
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}

