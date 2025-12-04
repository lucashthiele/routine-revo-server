package com.lucashthiele.routine_revo_server.infrastructure.data.workout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkoutJpaRepository extends JpaRepository<WorkoutSessionData, UUID> {
  
  @Query("SELECT ws FROM WorkoutSessionData ws " +
         "LEFT JOIN FETCH ws.items " +
         "WHERE ws.id = :id")
  Optional<WorkoutSessionData> findByIdWithItems(@Param("id") UUID id);
  
  @Query("SELECT ws FROM WorkoutSessionData ws " +
         "LEFT JOIN FETCH ws.items " +
         "WHERE ws.member.id = :memberId " +
         "ORDER BY ws.startedAt DESC")
  List<WorkoutSessionData> findAllByMemberId(@Param("memberId") UUID memberId);
  
  @Query("SELECT COUNT(ws) FROM WorkoutSessionData ws " +
         "WHERE ws.member.id = :memberId " +
         "AND ws.startedAt >= :since")
  long countByMemberIdAndStartedAtAfter(
      @Param("memberId") UUID memberId, 
      @Param("since") Instant since
  );
}

