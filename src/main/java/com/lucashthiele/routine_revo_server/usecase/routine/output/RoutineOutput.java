package com.lucashthiele.routine_revo_server.usecase.routine.output;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoutineOutput(
    UUID id,
    String name,
    String description,
    Instant expirationDate,
    Boolean isExpired,
    UUID creatorId,
    UUID memberId,
    RoutineType routineType,
    UUID templateId,
    Integer itemCount,
    List<RoutineItemOutput> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static RoutineOutput from(Routine routine) {
    Instant now = Instant.now();
    boolean isExpired = routine.getExpirationDate() != null && routine.getExpirationDate().isBefore(now);
    
    List<RoutineItemOutput> itemOutputs = routine.getItems() != null
        ? routine.getItems().stream()
            .map(item -> new RoutineItemOutput(
                item.getId(),
                item.getExerciseId(),
                item.getExerciseName(),
                item.getExerciseImageUrl(),
                item.getSets(),
                item.getReps(),
                item.getLoad(),
                item.getRestTime(),
                item.getSequenceOrder()
            ))
            .toList()
        : List.of();
    
    return new RoutineOutput(
        routine.getId(),
        routine.getName(),
        routine.getDescription(),
        routine.getExpirationDate(),
        isExpired,
        routine.getCreatorId(),
        routine.getMemberId(),
        routine.getRoutineType(),
        routine.getTemplateId(),
        itemOutputs.size(),
        itemOutputs,
        routine.getCreatedAt(),
        routine.getUpdatedAt()
    );
  }
}
