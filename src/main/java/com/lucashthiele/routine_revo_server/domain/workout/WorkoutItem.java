package com.lucashthiele.routine_revo_server.domain.workout;

import java.util.UUID;

public class WorkoutItem {
  private final UUID id;
  private final UUID exerciseId;
  private final String exerciseName;
  private final Integer setsDone;
  private final Integer repsDone;
  private final Double loadUsed;

  private WorkoutItem(Builder builder) {
    this.id = builder.id;
    this.exerciseId = builder.exerciseId;
    this.exerciseName = builder.exerciseName;
    this.setsDone = builder.setsDone;
    this.repsDone = builder.repsDone;
    this.loadUsed = builder.loadUsed;
  }

  public UUID getId() {
    return id;
  }

  public UUID getExerciseId() {
    return exerciseId;
  }

  public String getExerciseName() {
    return exerciseName;
  }

  public Integer getSetsDone() {
    return setsDone;
  }

  public Integer getRepsDone() {
    return repsDone;
  }

  public Double getLoadUsed() {
    return loadUsed;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID id;
    private UUID exerciseId;
    private String exerciseName;
    private Integer setsDone;
    private Integer repsDone;
    private Double loadUsed;

    public Builder id(UUID id) {
      this.id = id;
      return this;
    }

    public Builder exerciseId(UUID exerciseId) {
      this.exerciseId = exerciseId;
      return this;
    }

    public Builder exerciseName(String exerciseName) {
      this.exerciseName = exerciseName;
      return this;
    }

    public Builder setsDone(Integer setsDone) {
      this.setsDone = setsDone;
      return this;
    }

    public Builder repsDone(Integer repsDone) {
      this.repsDone = repsDone;
      return this;
    }

    public Builder loadUsed(Double loadUsed) {
      this.loadUsed = loadUsed;
      return this;
    }

    public WorkoutItem build() {
      return new WorkoutItem(this);
    }
  }
}

