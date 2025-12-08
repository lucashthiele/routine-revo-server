package com.lucashthiele.routine_revo_server.infrastructure.data.routine;

import com.lucashthiele.routine_revo_server.domain.routine.RoutineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoutineJpaRepository extends JpaRepository<RoutineData, UUID> {
  
  @Query("SELECT r FROM RoutineData r LEFT JOIN FETCH r.items WHERE r.id = :id")
  Optional<RoutineData> findByIdWithItems(@Param("id") UUID id);
  
  @Query("SELECT DISTINCT r FROM RoutineData r LEFT JOIN FETCH r.items WHERE r.member.id = :memberId ORDER BY r.createdAt DESC")
  List<RoutineData> findAllByMemberId(@Param("memberId") UUID memberId);
  
  @Query(value = "SELECT r FROM RoutineData r " +
         "WHERE (:creatorId IS NULL OR r.creator.id = :creatorId) " +
         "AND (:memberId IS NULL OR r.member.id = :memberId) " +
         "AND (:routineType IS NULL OR r.routineType = :routineType) " +
         "AND (:checkExpired = false OR " +
              "(:isExpired = true AND r.expirationDate IS NOT NULL AND r.expirationDate < :now) OR " +
              "(:isExpired = false AND (r.expirationDate IS NULL OR r.expirationDate >= :now))) " +
         "ORDER BY r.createdAt DESC",
         countQuery = "SELECT COUNT(r) FROM RoutineData r " +
         "WHERE (:creatorId IS NULL OR r.creator.id = :creatorId) " +
         "AND (:memberId IS NULL OR r.member.id = :memberId) " +
         "AND (:routineType IS NULL OR r.routineType = :routineType) " +
         "AND (:checkExpired = false OR " +
              "(:isExpired = true AND r.expirationDate IS NOT NULL AND r.expirationDate < :now) OR " +
              "(:isExpired = false AND (r.expirationDate IS NULL OR r.expirationDate >= :now)))")
  Page<RoutineData> findAllWithFilters(
      @Param("creatorId") UUID creatorId,
      @Param("memberId") UUID memberId,
      @Param("routineType") RoutineType routineType,
      @Param("checkExpired") boolean checkExpired,
      @Param("isExpired") boolean isExpired,
      @Param("now") Instant now,
      Pageable pageable
  );
  
  @Query("SELECT DISTINCT r FROM RoutineData r JOIN r.items i WHERE i.exercise.id = :exerciseId")
  List<RoutineData> findAllByExerciseId(@Param("exerciseId") UUID exerciseId);
}
