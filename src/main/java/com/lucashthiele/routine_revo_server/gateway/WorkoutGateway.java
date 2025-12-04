package com.lucashthiele.routine_revo_server.gateway;

import com.lucashthiele.routine_revo_server.domain.workout.WorkoutSession;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutGateway {
  UUID save(WorkoutSession session);
  
  Optional<WorkoutSession> findById(UUID id);
  
  List<WorkoutSession> findByMemberId(UUID memberId);
  
  long countWorkoutsByMemberSince(UUID memberId, Instant since);
}

