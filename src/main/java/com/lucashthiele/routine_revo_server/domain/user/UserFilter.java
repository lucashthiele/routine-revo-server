package com.lucashthiele.routine_revo_server.domain.user;

public record UserFilter(
    String name,
    Role role,
    Status status
) {
  public static UserFilter empty() {
    return new UserFilter(null, null, null);
  }
  
  public boolean isEmpty() {
    return name == null && role == null && status == null;
  }
}

