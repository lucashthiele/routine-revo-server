package com.lucashthiele.routine_revo_server.usecase.routine.exception;

import java.util.UUID;

public class RoutineNotFoundException extends RuntimeException {
  public RoutineNotFoundException(UUID id) {
    super("Routine not found with ID: " + id);
  }
}

