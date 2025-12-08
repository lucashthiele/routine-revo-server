package com.lucashthiele.routine_revo_server.domain.user;

import java.util.UUID;

public record UserFilter(
    String name,
    Role role,
    Status status,
    UUID coachId
) {
  public static UserFilter empty() {
    return new UserFilter(null, null, null, null);
  }
  
  public boolean isEmpty() {
    return name == null && role == null && status == null && coachId == null;
  }
}

