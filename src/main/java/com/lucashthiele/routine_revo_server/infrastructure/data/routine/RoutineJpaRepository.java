package com.lucashthiele.routine_revo_server.infrastructure.data.routine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoutineJpaRepository extends JpaRepository<RoutineData, UUID> {
  
  @Query("SELECT r FROM RoutineData r LEFT JOIN FETCH r.items WHERE r.id = :id")
  Optional<RoutineData> findByIdWithItems(@Param("id") UUID id);
  
  @Query("SELECT r FROM RoutineData r WHERE r.member.id = :memberId ORDER BY r.createdAt DESC")
  List<RoutineData> findAllByMemberId(@Param("memberId") UUID memberId);
}

