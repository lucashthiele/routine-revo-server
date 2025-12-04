package com.lucashthiele.routine_revo_server.infrastructure.data.workout;

import com.lucashthiele.routine_revo_server.domain.workout.WorkoutItem;
import com.lucashthiele.routine_revo_server.domain.workout.WorkoutSession;
import com.lucashthiele.routine_revo_server.infrastructure.data.exercise.ExerciseData;
import com.lucashthiele.routine_revo_server.infrastructure.data.routine.RoutineData;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.UserData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class WorkoutDataMapper {
  
  public WorkoutSession toDomain(WorkoutSessionData data) {
    if (data == null) return null;
    
    List<WorkoutItem> items = data.getItems() != null
        ? data.getItems().stream()
            .map(this::toItemDomain)
            .collect(Collectors.toList())
        : List.of();
    
    return WorkoutSession.builder()
        .id(data.getId())
        .memberId(data.getMember() != null ? data.getMember().getId() : null)
        .routineId(data.getRoutine() != null ? data.getRoutine().getId() : null)
        .startedAt(data.getStartedAt())
        .endedAt(data.getEndedAt())
        .items(items)
        .build();
  }
  
  public WorkoutItem toItemDomain(WorkoutItemData data) {
    if (data == null) return null;
    
    return WorkoutItem.builder()
        .id(data.getId())
        .exerciseId(data.getExercise() != null ? data.getExercise().getId() : null)
        .exerciseName(data.getExercise() != null ? data.getExercise().getName() : null)
        .setsDone(data.getSetsDone())
        .repsDone(data.getRepsDone())
        .loadUsed(data.getLoadUsed())
        .build();
  }
  
  public WorkoutSessionData toData(WorkoutSession session) {
    if (session == null) return null;
    
    WorkoutSessionData data = new WorkoutSessionData();
    data.setId(session.getId() != null ? session.getId() : UUID.randomUUID());
    data.setStartedAt(session.getStartedAt());
    data.setEndedAt(session.getEndedAt());
    
    // Set member relationship
    if (session.getMemberId() != null) {
      UserData member = new UserData();
      member.setId(session.getMemberId());
      data.setMember(member);
    }
    
    // Set routine relationship (optional)
    if (session.getRoutineId() != null) {
      RoutineData routine = new RoutineData();
      routine.setId(session.getRoutineId());
      data.setRoutine(routine);
    }
    
    // Convert items
    if (session.getItems() != null) {
      session.getItems().forEach(item -> {
        WorkoutItemData itemData = toItemData(item);
        data.addItem(itemData);
      });
    }
    
    return data;
  }
  
  public WorkoutItemData toItemData(WorkoutItem item) {
    if (item == null) return null;
    
    WorkoutItemData data = new WorkoutItemData();
    data.setId(item.getId() != null ? item.getId() : UUID.randomUUID());
    data.setSetsDone(item.getSetsDone());
    data.setRepsDone(item.getRepsDone());
    data.setLoadUsed(item.getLoadUsed());
    
    // Set exercise relationship
    if (item.getExerciseId() != null) {
      ExerciseData exercise = new ExerciseData();
      exercise.setId(item.getExerciseId());
      data.setExercise(exercise);
    }
    
    return data;
  }
}

