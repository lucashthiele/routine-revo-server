package com.lucashthiele.routine_revo_server.infrastructure.data.routine;

import com.lucashthiele.routine_revo_server.infrastructure.data.exercise.ExerciseData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "routine_items")
@Getter
@Setter
@NoArgsConstructor
public class RoutineItemData {
  @Id
  private UUID id;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "routine_id", nullable = false)
  private RoutineData routine;
  
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "exercise_id", nullable = false)
  private ExerciseData exercise;
  
  @Column(nullable = false)
  private Integer sets;
  
  @Column(nullable = false, length = 50)
  private String reps;
  
  @Column
  private Double load;
  
  @Column(name = "rest_time", length = 50)
  private String restTime;
  
  @Column(name = "sequence_order", nullable = false)
  private Integer sequenceOrder;
}

