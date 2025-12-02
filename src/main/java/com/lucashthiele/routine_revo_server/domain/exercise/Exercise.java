package com.lucashthiele.routine_revo_server.domain.exercise;

import java.time.LocalDateTime;
import java.util.UUID;

public class Exercise {
  private final UUID id;
  private final String name;
  private final MuscleGroup muscleGroup;
  private final String description;
  private final String equipment;
  private final String imageUrl;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  private Exercise(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.muscleGroup = builder.muscleGroup;
    this.description = builder.description;
    this.equipment = builder.equipment;
    this.imageUrl = builder.imageUrl;
    this.createdAt = builder.createdAt;
    this.updatedAt = builder.updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public MuscleGroup getMuscleGroup() {
    return muscleGroup;
  }

  public String getDescription() {
    return description;
  }

  public String getEquipment() {
    return equipment;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID id;
    private String name;
    private MuscleGroup muscleGroup;
    private String description;
    private String equipment;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Builder id(UUID id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder muscleGroup(MuscleGroup muscleGroup) {
      this.muscleGroup = muscleGroup;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder equipment(String equipment) {
      this.equipment = equipment;
      return this;
    }

    public Builder imageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    public Builder createdAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder updatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Exercise build() {
      return new Exercise(this);
    }
  }
}

