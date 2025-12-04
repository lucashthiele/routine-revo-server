package com.lucashthiele.routine_revo_server.domain.workout;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class WorkoutSession {
  private final UUID id;
  private final UUID memberId;
  private final UUID routineId;
  private final Instant startedAt;
  private final Instant endedAt;
  private final List<WorkoutItem> items;

  private WorkoutSession(Builder builder) {
    this.id = builder.id;
    this.memberId = builder.memberId;
    this.routineId = builder.routineId;
    this.startedAt = builder.startedAt;
    this.endedAt = builder.endedAt;
    this.items = builder.items;
  }

  public UUID getId() {
    return id;
  }

  public UUID getMemberId() {
    return memberId;
  }

  public UUID getRoutineId() {
    return routineId;
  }

  public Instant getStartedAt() {
    return startedAt;
  }

  public Instant getEndedAt() {
    return endedAt;
  }

  public List<WorkoutItem> getItems() {
    return items;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID id;
    private UUID memberId;
    private UUID routineId;
    private Instant startedAt;
    private Instant endedAt;
    private List<WorkoutItem> items;

    public Builder id(UUID id) {
      this.id = id;
      return this;
    }

    public Builder memberId(UUID memberId) {
      this.memberId = memberId;
      return this;
    }

    public Builder routineId(UUID routineId) {
      this.routineId = routineId;
      return this;
    }

    public Builder startedAt(Instant startedAt) {
      this.startedAt = startedAt;
      return this;
    }

    public Builder endedAt(Instant endedAt) {
      this.endedAt = endedAt;
      return this;
    }

    public Builder items(List<WorkoutItem> items) {
      this.items = items;
      return this;
    }

    public WorkoutSession build() {
      return new WorkoutSession(this);
    }
  }
}

