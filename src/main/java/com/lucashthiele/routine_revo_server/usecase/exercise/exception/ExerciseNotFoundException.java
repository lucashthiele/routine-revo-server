package com.lucashthiele.routine_revo_server.usecase.exercise.exception;

import java.util.UUID;

public class ExerciseNotFoundException extends RuntimeException {
  public ExerciseNotFoundException(UUID id) {
    super("Exercise not found with ID: " + id);
  }
}

