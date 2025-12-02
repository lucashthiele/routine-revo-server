package com.lucashthiele.routine_revo_server.infrastructure.web.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucashthiele.routine_revo_server.domain.exercise.MuscleGroup;
import com.lucashthiele.routine_revo_server.infrastructure.web.exercise.dto.*;
import com.lucashthiele.routine_revo_server.usecase.exercise.*;
import com.lucashthiele.routine_revo_server.usecase.exercise.input.*;
import com.lucashthiele.routine_revo_server.usecase.exercise.output.ExerciseOutput;
import com.lucashthiele.routine_revo_server.usecase.exercise.output.SearchExercisesOutput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/exercises")
public class ExerciseController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseController.class);
  
  private final CreateExerciseUseCase createExerciseUseCase;
  private final UpdateExerciseUseCase updateExerciseUseCase;
  private final DeleteExerciseUseCase deleteExerciseUseCase;
  private final GetExerciseByIdUseCase getExerciseByIdUseCase;
  private final SearchExercisesUseCase searchExercisesUseCase;
  private final ObjectMapper objectMapper;
  
  public ExerciseController(
      CreateExerciseUseCase createExerciseUseCase,
      UpdateExerciseUseCase updateExerciseUseCase,
      DeleteExerciseUseCase deleteExerciseUseCase,
      GetExerciseByIdUseCase getExerciseByIdUseCase,
      SearchExercisesUseCase searchExercisesUseCase,
      ObjectMapper objectMapper) {
    this.createExerciseUseCase = createExerciseUseCase;
    this.updateExerciseUseCase = updateExerciseUseCase;
    this.deleteExerciseUseCase = deleteExerciseUseCase;
    this.getExerciseByIdUseCase = getExerciseByIdUseCase;
    this.searchExercisesUseCase = searchExercisesUseCase;
    this.objectMapper = objectMapper;
  }
  
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<CreateExerciseResponse> createExercise(
      @RequestParam("data") String dataJson,
      @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
    
    LOGGER.info("POST /api/v1/exercises - Create exercise request received");
    
    // Parse JSON data
    CreateExerciseRequest request = objectMapper.readValue(dataJson, CreateExerciseRequest.class);
    
    // Validate request
    if (request.name() == null || request.name().isBlank()) {
      return ResponseEntity.badRequest().body(
          new CreateExerciseResponse(null, "Name is required")
      );
    }
    
    if (request.muscleGroup() == null) {
      return ResponseEntity.badRequest().body(
          new CreateExerciseResponse(null, "Muscle group is required")
      );
    }
    
    // Build input
    CreateExerciseInput input = new CreateExerciseInput(
        request.name(),
        request.muscleGroup(),
        request.description(),
        request.equipment(),
        file != null ? file.getInputStream() : null,
        file != null ? file.getOriginalFilename() : null,
        file != null ? file.getContentType() : null,
        file != null ? file.getSize() : 0
    );
    
    UUID exerciseId = createExerciseUseCase.execute(input);
    
    LOGGER.info("POST /api/v1/exercises - Exercise created successfully with ID: {}", exerciseId);
    
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreateExerciseResponse(exerciseId, "Exercise created successfully"));
  }
  
  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ExerciseResponse> updateExercise(
      @PathVariable UUID id,
      @RequestParam("data") String dataJson,
      @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
    
    LOGGER.info("PUT /api/v1/exercises/{} - Update exercise request received", id);
    
    // Parse JSON data
    UpdateExerciseRequest request = objectMapper.readValue(dataJson, UpdateExerciseRequest.class);
    
    // Validate request
    if (request.name() == null || request.name().isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    
    if (request.muscleGroup() == null) {
      return ResponseEntity.badRequest().build();
    }
    
    // Build input
    UpdateExerciseInput input = new UpdateExerciseInput(
        id,
        request.name(),
        request.muscleGroup(),
        request.description(),
        request.equipment(),
        file != null ? file.getInputStream() : null,
        file != null ? file.getOriginalFilename() : null,
        file != null ? file.getContentType() : null,
        file != null ? file.getSize() : null
    );
    
    ExerciseOutput output = updateExerciseUseCase.execute(input);
    
    ExerciseResponse response = new ExerciseResponse(
        output.id(),
        output.name(),
        output.muscleGroup(),
        output.description(),
        output.equipment(),
        output.imageUrl(),
        output.createdAt(),
        output.updatedAt()
    );
    
    LOGGER.info("PUT /api/v1/exercises/{} - Exercise updated successfully", id);
    
    return ResponseEntity.ok(response);
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteExercise(@PathVariable UUID id) {
    LOGGER.info("DELETE /api/v1/exercises/{} - Delete exercise request received", id);
    
    DeleteExerciseInput input = new DeleteExerciseInput(id);
    deleteExerciseUseCase.execute(input);
    
    LOGGER.info("DELETE /api/v1/exercises/{} - Exercise deleted successfully", id);
    
    return ResponseEntity.noContent().build();
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<ExerciseResponse> getExerciseById(@PathVariable UUID id) {
    LOGGER.info("GET /api/v1/exercises/{} - Get exercise by ID request received", id);
    
    GetExerciseByIdInput input = new GetExerciseByIdInput(id);
    ExerciseOutput output = getExerciseByIdUseCase.execute(input);
    
    ExerciseResponse response = new ExerciseResponse(
        output.id(),
        output.name(),
        output.muscleGroup(),
        output.description(),
        output.equipment(),
        output.imageUrl(),
        output.createdAt(),
        output.updatedAt()
    );
    
    LOGGER.info("GET /api/v1/exercises/{} - Exercise found and returned", id);
    
    return ResponseEntity.ok(response);
  }
  
  @GetMapping
  public ResponseEntity<SearchExercisesResponse> searchExercises(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) MuscleGroup muscleGroup,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size) {
    
    LOGGER.info("GET /api/v1/exercises - Search exercises request received");
    
    SearchExercisesRequest request = new SearchExercisesRequest(name, muscleGroup, page, size);
    
    SearchExercisesInput input = new SearchExercisesInput(
        request.name(),
        request.muscleGroup(),
        request.getPage(),
        request.getSize()
    );
    
    SearchExercisesOutput output = searchExercisesUseCase.execute(input);
    
    SearchExercisesResponse response = new SearchExercisesResponse(
        output.exercises().stream()
            .map(e -> new ExerciseResponse(
                e.id(),
                e.name(),
                e.muscleGroup(),
                e.description(),
                e.equipment(),
                e.imageUrl(),
                e.createdAt(),
                e.updatedAt()
            ))
            .toList(),
        output.total(),
        output.page(),
        output.size(),
        output.totalPages()
    );
    
    LOGGER.info("GET /api/v1/exercises - Returning {} exercises", response.exercises().size());
    
    return ResponseEntity.ok(response);
  }
}

