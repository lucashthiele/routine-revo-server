package com.lucashthiele.routine_revo_server.domain.user;

import lombok.Getter;

import java.util.UUID;

@Getter
public class User {
  private final UUID id;
  private final String name;
  private final String email;
  private final String password;
  private final Role role;
  private final Status status;
  private final UUID coachId;
  private final int workoutPerWeek;
  
  
  private User(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.email = builder.email;
    this.password = builder.password;
    this.role = builder.role;
    this.status = builder.status;
    this.coachId = builder.coachId;
    this.workoutPerWeek = builder.workoutPerWeek;
  }

  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private Status status;
    private UUID coachId;
    private int workoutPerWeek;

    public Builder id(UUID id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public Builder role(Role role) {
      this.role = role;
      return this;
    }

    public Builder status(Status status) {
      this.status = status;
      return this;
    }
    public Builder workoutPerWeek(int workoutPerWeek) {
      this.workoutPerWeek = workoutPerWeek;
      return this;
    }
    public Builder coachId(UUID coachId) {
      this.coachId = coachId;
      return this;
    }
    
    public User build() {
      return new User(this);
    }
  }
}