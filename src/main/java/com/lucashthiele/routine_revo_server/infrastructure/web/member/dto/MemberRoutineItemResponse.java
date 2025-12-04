package com.lucashthiele.routine_revo_server.infrastructure.web.member.dto;

import java.util.UUID;

public record MemberRoutineItemResponse(
    UUID id,
    UUID exerciseId,
    String exerciseName,
    String exerciseImageUrl,
    Integer sets,
    String reps,
    Double load,
    String restTime,
    Integer sequenceOrder
) {
}

