package com.lucashthiele.routine_revo_server.domain.routine;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Routine {
  private final UUID id;
  private final String name;
  private final String description;
  private final Instant expirationDate;
  private final UUID creatorId;
  private final UUID memberId;
  private final List<RoutineItem> items;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  private Routine(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.expirationDate = builder.expirationDate;
    this.creatorId = builder.creatorId;
    this.memberId = builder.memberId;
    this.items = builder.items;
    this.createdAt = builder.createdAt;
    this.updatedAt = builder.updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Instant getExpirationDate() {
    return expirationDate;
  }

  public UUID getCreatorId() {
    return creatorId;
  }

  public UUID getMemberId() {
    return memberId;
  }

  public List<RoutineItem> getItems() {
    return items;
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
    private String description;
    private Instant expirationDate;
    private UUID creatorId;
    private UUID memberId;
    private List<RoutineItem> items;
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

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder expirationDate(Instant expirationDate) {
      this.expirationDate = expirationDate;
      return this;
    }

    public Builder creatorId(UUID creatorId) {
      this.creatorId = creatorId;
      return this;
    }

    public Builder memberId(UUID memberId) {
      this.memberId = memberId;
      return this;
    }

    public Builder items(List<RoutineItem> items) {
      this.items = items;
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

    public Routine build() {
      return new Routine(this);
    }
  }
}

