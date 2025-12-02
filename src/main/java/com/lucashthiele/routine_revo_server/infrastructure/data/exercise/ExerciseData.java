package com.lucashthiele.routine_revo_server.infrastructure.data.exercise;

import com.lucashthiele.routine_revo_server.infrastructure.data.exercise.enums.MuscleGroupData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
public class ExerciseData {
  @Id
  private UUID id;
  
  @Column(nullable = false)
  private String name;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "muscle_group", nullable = false, columnDefinition = "muscle_group")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private MuscleGroupData muscleGroup;
  
  @Column(columnDefinition = "TEXT")
  private String description;
  
  @Column
  private String equipment;
  
  @Column(name = "image_url", length = 500)
  private String imageUrl;
  
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
  
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
  
  @PrePersist
  protected void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }
  
  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}

