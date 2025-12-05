package com.lucashthiele.routine_revo_server.infrastructure.data.routine;

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
  
  @Query("SELECT r FROM RoutineData r WHERE r.member.id = :memberId ORDER BY r.createdAt DESC")
  List<RoutineData> findAllByMemberId(@Param("memberId") UUID memberId);
  
  @Query(value = "SELECT r FROM RoutineData r " +
         "WHERE (:creatorId IS NULL OR r.creator.id = :creatorId) " +
         "AND (:memberId IS NULL OR r.member.id = :memberId) " +
         "AND (:templatesOnly = false OR r.member IS NULL) " +
         "AND (:checkExpired = false OR " +
              "(:isExpired = true AND r.expirationDate IS NOT NULL AND r.expirationDate < :now) OR " +
              "(:isExpired = false AND (r.expirationDate IS NULL OR r.expirationDate >= :now))) " +
         "ORDER BY r.createdAt DESC",
         countQuery = "SELECT COUNT(r) FROM RoutineData r " +
         "WHERE (:creatorId IS NULL OR r.creator.id = :creatorId) " +
         "AND (:memberId IS NULL OR r.member.id = :memberId) " +
         "AND (:templatesOnly = false OR r.member IS NULL) " +
         "AND (:checkExpired = false OR " +
              "(:isExpired = true AND r.expirationDate IS NOT NULL AND r.expirationDate < :now) OR " +
              "(:isExpired = false AND (r.expirationDate IS NULL OR r.expirationDate >= :now)))")
  Page<RoutineData> findAllWithFilters(
      @Param("creatorId") UUID creatorId,
      @Param("memberId") UUID memberId,
      @Param("templatesOnly") boolean templatesOnly,
      @Param("checkExpired") boolean checkExpired,
      @Param("isExpired") boolean isExpired,
      @Param("now") Instant now,
      Pageable pageable
  );
}
