package com.lucashthiele.routine_revo_server.usecase.exercise.exception;

import java.util.List;
import java.util.UUID;

public class ExerciseInUseException extends RuntimeException {
  private final UUID exerciseId;
  private final List<String> routineNames;
  
  public ExerciseInUseException(UUID exerciseId, List<String> routineNames) {
    super(buildMessage(routineNames));
    this.exerciseId = exerciseId;
    this.routineNames = routineNames;
  }
  
  private static String buildMessage(List<String> routineNames) {
    if (routineNames.size() == 1) {
      return String.format(
          "Não é possível excluir este exercício pois ele está sendo utilizado na rotina \"%s\". " +
          "Remova o exercício da rotina antes de excluí-lo.",
          routineNames.get(0)
      );
    }
    
    String routineList = String.join(", ", routineNames);
    return String.format(
        "Não é possível excluir este exercício pois ele está sendo utilizado nas seguintes rotinas: %s. " +
        "Remova o exercício dessas rotinas antes de excluí-lo.",
        routineList
    );
  }
  
  public UUID getExerciseId() {
    return exerciseId;
  }
  
  public List<String> getRoutineNames() {
    return routineNames;
  }
}

