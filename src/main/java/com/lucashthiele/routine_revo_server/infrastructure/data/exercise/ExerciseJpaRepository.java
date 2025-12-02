package com.lucashthiele.routine_revo_server.infrastructure.data.exercise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExerciseJpaRepository extends JpaRepository<ExerciseData, UUID>, JpaSpecificationExecutor<ExerciseData> {
}

