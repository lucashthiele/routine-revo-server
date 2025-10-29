package com.lucashthiele.routine_revo_server.domain.user;

import java.util.UUID;

public class User {
  private UUID id;
  private String name;
  private String email;
  private String password;
  private Role role;
  private Status status;

  public User(UUID id, String name, String email, String password, Role role, Status status) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role;
    this.status = status;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public Role getRole() {
    return role;
  }

  public Status getStatus() {
    return status;
  }
}
