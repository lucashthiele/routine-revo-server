package com.lucashthiele.routine_revo_server.infrastructure.data.routine;

import com.lucashthiele.routine_revo_server.domain.routine.RoutineType;
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
@Table(name = "routines")
@Getter
@Setter
@NoArgsConstructor
public class RoutineData {
  @Id
  private UUID id;
  
  @Column(nullable = false)
  private String name;
  
  @Column(columnDefinition = "TEXT")
  private String description;
  
  @Column(name = "expiration_date")
  private Instant expirationDate;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", nullable = false)
  private UserData creator;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private UserData member;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "routine_type", nullable = false, length = 20)
  private RoutineType routineType = RoutineType.CUSTOM;
  
  @Column(name = "template_id")
  private UUID templateId;
  
  @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @OrderBy("sequenceOrder ASC")
  private List<RoutineItemData> items = new ArrayList<>();
  
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
  public void addItem(RoutineItemData item) {
    items.add(item);
    item.setRoutine(this);
  }
  
  public void removeItem(RoutineItemData item) {
    items.remove(item);
    item.setRoutine(null);
  }
  
  public void clearItems() {
    items.forEach(item -> item.setRoutine(null));
    items.clear();
  }
}

