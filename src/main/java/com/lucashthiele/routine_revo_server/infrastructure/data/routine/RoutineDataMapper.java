package com.lucashthiele.routine_revo_server.infrastructure.data.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineItem;
import com.lucashthiele.routine_revo_server.infrastructure.data.exercise.ExerciseData;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.UserData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RoutineDataMapper {
  
  public Routine toDomain(RoutineData routineData) {
    if (routineData == null) return null;
    
    List<RoutineItem> items = routineData.getItems() != null
        ? routineData.getItems().stream()
            .map(this::itemToDomain)
            .collect(Collectors.toList())
        : List.of();
    
    return Routine.builder()
        .id(routineData.getId())
        .name(routineData.getName())
        .description(routineData.getDescription())
        .expirationDate(routineData.getExpirationDate())
        .creatorId(routineData.getCreator() != null ? routineData.getCreator().getId() : null)
        .memberId(routineData.getMember() != null ? routineData.getMember().getId() : null)
        .items(items)
        .createdAt(routineData.getCreatedAt())
        .updatedAt(routineData.getUpdatedAt())
        .build();
  }
  
  public RoutineData toData(Routine routine) {
    if (routine == null) return null;
    
    RoutineData routineData = new RoutineData();
    routineData.setId(routine.getId());
    routineData.setName(routine.getName());
    routineData.setDescription(routine.getDescription());
    routineData.setExpirationDate(routine.getExpirationDate());
    
    // Set creator relationship
    if (routine.getCreatorId() != null) {
      UserData creator = new UserData();
      creator.setId(routine.getCreatorId());
      routineData.setCreator(creator);
    }
    
    // Set member relationship
    if (routine.getMemberId() != null) {
      UserData member = new UserData();
      member.setId(routine.getMemberId());
      routineData.setMember(member);
    }
    
    // Convert items
    if (routine.getItems() != null) {
      List<RoutineItemData> itemDataList = routine.getItems().stream()
          .map(item -> itemToData(item, routineData))
          .collect(Collectors.toList());
      itemDataList.forEach(routineData::addItem);
    }
    
    return routineData;
  }
  
  private RoutineItem itemToDomain(RoutineItemData itemData) {
    if (itemData == null) return null;
    
    return RoutineItem.builder()
        .id(itemData.getId())
        .exerciseId(itemData.getExercise() != null ? itemData.getExercise().getId() : null)
        .exerciseName(itemData.getExercise() != null ? itemData.getExercise().getName() : null)
        .exerciseImageUrl(itemData.getExercise() != null ? itemData.getExercise().getImageUrl() : null)
        .sets(itemData.getSets())
        .reps(itemData.getReps())
        .load(itemData.getLoad())
        .restTime(itemData.getRestTime())
        .sequenceOrder(itemData.getSequenceOrder())
        .build();
  }
  
  private RoutineItemData itemToData(RoutineItem item, RoutineData routineData) {
    if (item == null) return null;
    
    RoutineItemData itemData = new RoutineItemData();
    itemData.setId(item.getId() != null ? item.getId() : UUID.randomUUID());
    itemData.setRoutine(routineData);
    
    // Set exercise relationship
    if (item.getExerciseId() != null) {
      ExerciseData exercise = new ExerciseData();
      exercise.setId(item.getExerciseId());
      itemData.setExercise(exercise);
    }
    
    itemData.setSets(item.getSets());
    itemData.setReps(item.getReps());
    itemData.setLoad(item.getLoad());
    itemData.setRestTime(item.getRestTime());
    itemData.setSequenceOrder(item.getSequenceOrder());
    
    return itemData;
  }
}

