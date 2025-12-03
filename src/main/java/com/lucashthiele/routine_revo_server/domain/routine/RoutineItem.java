package com.lucashthiele.routine_revo_server.domain.routine;

import java.util.UUID;

public class RoutineItem {
  private final UUID id;
  private final UUID exerciseId;
  private final String exerciseName;
  private final String exerciseImageUrl;
  private final Integer sets;
  private final String reps;
  private final Double load;
  private final String restTime;
  private final Integer sequenceOrder;

  private RoutineItem(Builder builder) {
    this.id = builder.id;
    this.exerciseId = builder.exerciseId;
    this.exerciseName = builder.exerciseName;
    this.exerciseImageUrl = builder.exerciseImageUrl;
    this.sets = builder.sets;
    this.reps = builder.reps;
    this.load = builder.load;
    this.restTime = builder.restTime;
    this.sequenceOrder = builder.sequenceOrder;
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

  public String getExerciseImageUrl() {
    return exerciseImageUrl;
  }

  public Integer getSets() {
    return sets;
  }

  public String getReps() {
    return reps;
  }

  public Double getLoad() {
    return load;
  }

  public String getRestTime() {
    return restTime;
  }

  public Integer getSequenceOrder() {
    return sequenceOrder;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID id;
    private UUID exerciseId;
    private String exerciseName;
    private String exerciseImageUrl;
    private Integer sets;
    private String reps;
    private Double load;
    private String restTime;
    private Integer sequenceOrder;

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

    public Builder exerciseImageUrl(String exerciseImageUrl) {
      this.exerciseImageUrl = exerciseImageUrl;
      return this;
    }

    public Builder sets(Integer sets) {
      this.sets = sets;
      return this;
    }

    public Builder reps(String reps) {
      this.reps = reps;
      return this;
    }

    public Builder load(Double load) {
      this.load = load;
      return this;
    }

    public Builder restTime(String restTime) {
      this.restTime = restTime;
      return this;
    }

    public Builder sequenceOrder(Integer sequenceOrder) {
      this.sequenceOrder = sequenceOrder;
      return this;
    }

    public RoutineItem build() {
      return new RoutineItem(this);
    }
  }
}

