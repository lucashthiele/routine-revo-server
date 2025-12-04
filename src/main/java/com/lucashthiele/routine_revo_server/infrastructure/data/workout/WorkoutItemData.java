package com.lucashthiele.routine_revo_server.infrastructure.data.workout;

import com.lucashthiele.routine_revo_server.infrastructure.data.exercise.ExerciseData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "workout_items")
@Getter
@Setter
@NoArgsConstructor
public class WorkoutItemData {
  @Id
  private UUID id;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "workout_session_id", nullable = false)
  private WorkoutSessionData workoutSession;
  
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "exercise_id", nullable = false)
  private ExerciseData exercise;
  
  @Column(name = "sets_done", nullable = false)
  private Integer setsDone;
  
  @Column(name = "reps_done", nullable = false)
  private Integer repsDone;
  
  @Column(name = "load_used")
  private Double loadUsed;
}

