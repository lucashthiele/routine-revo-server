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
  
  /**
   * Fetches the most recent workout sessions for a member, ordered by endedAt descending.
   * 
   * @param memberId the member's UUID
   * @param limit maximum number of sessions to return
   * @return list of recent workout sessions
   */
  List<WorkoutSession> findRecentByMemberId(UUID memberId, int limit);
  
  long countWorkoutsByMemberSince(UUID memberId, Instant since);
}

