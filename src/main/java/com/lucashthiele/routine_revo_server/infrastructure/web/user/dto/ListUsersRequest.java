package com.lucashthiele.routine_revo_server.infrastructure.web.user.dto;

import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;

import java.util.UUID;

public record ListUsersRequest(
    String name,
    Role role,
    Status status,
    UUID coachId,
    Integer page,
    Integer size
) {
  public int getPage() {
    return page != null && page >= 0 ? page : 0;
  }
  
  public int getSize() {
    return size != null && size > 0 ? size : 20;
  }
}

