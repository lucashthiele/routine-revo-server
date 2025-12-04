package com.lucashthiele.routine_revo_server.infrastructure.data.workout;

import com.lucashthiele.routine_revo_server.infrastructure.data.routine.RoutineData;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.UserData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workout_sessions")
@Getter
@Setter
@NoArgsConstructor
public class WorkoutSessionData {
  @Id
  private UUID id;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private UserData member;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "routine_id")
  private RoutineData routine;
  
  @Column(name = "started_at", nullable = false)
  private Instant startedAt;
  
  @Column(name = "ended_at")
  private Instant endedAt;
  
  @OneToMany(mappedBy = "workoutSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<WorkoutItemData> items = new ArrayList<>();
  
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
  
  // Helper method to manage bidirectional relationship
  public void addItem(WorkoutItemData item) {
    items.add(item);
    item.setWorkoutSession(this);
  }
  
  public void removeItem(WorkoutItemData item) {
    items.remove(item);
    item.setWorkoutSession(null);
  }
  
  public void clearItems() {
    items.forEach(item -> item.setWorkoutSession(null));
    items.clear();
  }
}

