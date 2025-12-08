package com.lucashthiele.routine_revo_server.infrastructure.web.user;

import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.infrastructure.web.user.dto.*;
import com.lucashthiele.routine_revo_server.usecase.user.CreateUserUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.GetUserByIdUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.InactivateUserUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.LinkCoachUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.ListUsersUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.SearchMembersUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.UpdateUserUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.input.CreateUserInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.GetUserByIdInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.InactivateUserInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.LinkCoachInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.ListUsersInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.SearchMembersInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.UpdateUserInput;
import com.lucashthiele.routine_revo_server.usecase.user.output.AssignedRoutineOutput;
import com.lucashthiele.routine_revo_server.usecase.user.output.ListUsersOutput;
import com.lucashthiele.routine_revo_server.usecase.user.output.UserOutput;
import com.lucashthiele.routine_revo_server.usecase.routine.BulkAssignRoutinesUseCase;
import com.lucashthiele.routine_revo_server.usecase.routine.BulkDeleteRoutinesUseCase;
import com.lucashthiele.routine_revo_server.usecase.routine.BulkSyncRoutinesUseCase;
import com.lucashthiele.routine_revo_server.usecase.routine.input.BulkAssignRoutinesInput;
import com.lucashthiele.routine_revo_server.usecase.routine.input.BulkDeleteRoutinesInput;
import com.lucashthiele.routine_revo_server.usecase.routine.input.BulkSyncRoutinesInput;
import com.lucashthiele.routine_revo_server.usecase.routine.output.BulkAssignRoutinesOutput;
import com.lucashthiele.routine_revo_server.usecase.routine.output.BulkDeleteRoutinesOutput;
import com.lucashthiele.routine_revo_server.usecase.routine.output.BulkSyncRoutinesOutput;
import com.lucashthiele.routine_revo_server.infrastructure.web.user.dto.BulkAssignRoutinesRequest;
import com.lucashthiele.routine_revo_server.infrastructure.web.user.dto.BulkAssignRoutinesResponse;
import com.lucashthiele.routine_revo_server.infrastructure.web.user.dto.BulkDeleteRoutinesRequest;
import com.lucashthiele.routine_revo_server.infrastructure.web.user.dto.BulkDeleteRoutinesResponse;
import com.lucashthiele.routine_revo_server.infrastructure.web.user.dto.BulkSyncRoutinesRequest;
import com.lucashthiele.routine_revo_server.infrastructure.web.user.dto.BulkSyncRoutinesResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  private final CreateUserUseCase createUserUseCase;
  private final ListUsersUseCase listUsersUseCase;
  private final GetUserByIdUseCase getUserByIdUseCase;
  private final UpdateUserUseCase updateUserUseCase;
  private final InactivateUserUseCase inactivateUserUseCase;
  private final LinkCoachUseCase linkCoachUseCase;
  private final SearchMembersUseCase searchMembersUseCase;
  private final BulkAssignRoutinesUseCase bulkAssignRoutinesUseCase;
  private final BulkDeleteRoutinesUseCase bulkDeleteRoutinesUseCase;
  private final BulkSyncRoutinesUseCase bulkSyncRoutinesUseCase;

  public UserController(CreateUserUseCase createUserUseCase,
                        ListUsersUseCase listUsersUseCase,
                        GetUserByIdUseCase getUserByIdUseCase,
                        UpdateUserUseCase updateUserUseCase,
                        InactivateUserUseCase inactivateUserUseCase,
                        LinkCoachUseCase linkCoachUseCase,
                        SearchMembersUseCase searchMembersUseCase,
                        BulkAssignRoutinesUseCase bulkAssignRoutinesUseCase,
                        BulkDeleteRoutinesUseCase bulkDeleteRoutinesUseCase,
                        BulkSyncRoutinesUseCase bulkSyncRoutinesUseCase) {
    this.createUserUseCase = createUserUseCase;
    this.listUsersUseCase = listUsersUseCase;
    this.getUserByIdUseCase = getUserByIdUseCase;
    this.updateUserUseCase = updateUserUseCase;
    this.inactivateUserUseCase = inactivateUserUseCase;
    this.linkCoachUseCase = linkCoachUseCase;
    this.searchMembersUseCase = searchMembersUseCase;
    this.bulkAssignRoutinesUseCase = bulkAssignRoutinesUseCase;
    this.bulkDeleteRoutinesUseCase = bulkDeleteRoutinesUseCase;
    this.bulkSyncRoutinesUseCase = bulkSyncRoutinesUseCase;
  }
  
  @PostMapping
  public ResponseEntity<Void> createUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @Valid @RequestBody CreateUserRequest request) {
    LOGGER.info("POST /api/v1/users - User creation request received");

    CreateUserInput input = new CreateUserInput(request.name(),
        request.email(),
        request.role(),
        request.coachId());

    createUserUseCase.execute(input);

    LOGGER.info("POST /api/v1/users - User created successfully");
    return ResponseEntity.ok().build();
  }
  
  @GetMapping
  public ResponseEntity<ListUsersResponse> listUsers(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Role role,
      @RequestParam(required = false) Status status,
      @RequestParam(required = false) UUID coachId,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size) {
    
    LOGGER.info("GET /api/v1/users - List users request received");
    
    ListUsersRequest request = new ListUsersRequest(name, role, status, coachId, page, size);
    
    ListUsersInput input = new ListUsersInput(
        request.name(),
        request.role(),
        request.status(),
        request.coachId(),
        request.getPage(),
        request.getSize()
    );
    
    ListUsersOutput output = listUsersUseCase.execute(input);
    
    ListUsersResponse response = new ListUsersResponse(
        output.users().stream()
            .map(this::toUserResponse)
            .toList(),
        output.total(),
        output.page(),
        output.size(),
        output.totalPages()
    );
    
    LOGGER.info("GET /api/v1/users - Returning {} users", response.users().size());
    return ResponseEntity.ok(response);
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
    LOGGER.info("GET /api/v1/users/{} - Get user by ID request received", id);
    
    GetUserByIdInput input = new GetUserByIdInput(id);
    
    UserOutput output = getUserByIdUseCase.execute(input);
    
    UserResponse response = toUserResponse(output);
    
    LOGGER.info("GET /api/v1/users/{} - User found and returned", id);
    return ResponseEntity.ok(response);
  }
  
  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateUserRequest request) {
    LOGGER.info("PUT /api/v1/users/{} - Update user request received", id);
    
    UpdateUserInput input = new UpdateUserInput(id, request.name(), request.email());
    
    UserOutput output = updateUserUseCase.execute(input);
    
    UserResponse response = toUserResponse(output);
    
    LOGGER.info("PUT /api/v1/users/{} - User updated successfully", id);
    return ResponseEntity.ok(response);
  }
  
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> inactivateUser(@PathVariable UUID id) {
    LOGGER.info("DELETE /api/v1/users/{} - Inactivate user request received", id);
    
    InactivateUserInput input = new InactivateUserInput(id);
    
    inactivateUserUseCase.execute(input);
    
    LOGGER.info("DELETE /api/v1/users/{} - User inactivated successfully", id);
    return ResponseEntity.noContent().build();
  }
  
  @PatchMapping("/{userId}/coach")
  public ResponseEntity<Void> linkCoach(@PathVariable UUID userId,
                                        @Valid @RequestBody LinkCoachRequest request) {
    LOGGER.info("PATCH /api/v1/users/{}/coach - Link coach request received", userId);
    
    LinkCoachInput input = new LinkCoachInput(request.coachId(), userId);
    
    linkCoachUseCase.execute(input);
    
    LOGGER.info("PATCH /api/v1/users/{}/coach - Coach linked successfully", userId);
    return ResponseEntity.ok().build();
  }
  
  @GetMapping("/members")
  public ResponseEntity<ListUsersResponse> searchMembers(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size) {
    
    LOGGER.info("GET /api/v1/users/members - Search active members request received");
    
    SearchMembersInput input = new SearchMembersInput(name, page, size);
    
    ListUsersOutput output = searchMembersUseCase.execute(input);
    
    ListUsersResponse response = new ListUsersResponse(
        output.users().stream()
            .map(this::toUserResponse)
            .toList(),
        output.total(),
        output.page(),
        output.size(),
        output.totalPages()
    );
    
    LOGGER.info("GET /api/v1/users/members - Returning {} active members", response.users().size());
    return ResponseEntity.ok(response);
  }
  
  @PostMapping("/members/{memberId}/routines/bulk")
  public ResponseEntity<BulkAssignRoutinesResponse> bulkAssignRoutines(
      @PathVariable UUID memberId,
      @Valid @RequestBody BulkAssignRoutinesRequest request) {
    LOGGER.info("POST /api/v1/users/members/{}/routines/bulk - Bulk assign routines request received", memberId);
    
    BulkAssignRoutinesInput input = new BulkAssignRoutinesInput(
        memberId,
        request.routineIds(),
        request.expirationDate()
    );
    
    BulkAssignRoutinesOutput output = bulkAssignRoutinesUseCase.execute(input);
    
    BulkAssignRoutinesResponse response = new BulkAssignRoutinesResponse(
        output.assignedCount(),
        output.message()
    );
    
    LOGGER.info("POST /api/v1/users/members/{}/routines/bulk - {}", memberId, output.message());
    return ResponseEntity.ok(response);
  }
  
  @DeleteMapping("/members/{memberId}/routines/bulk")
  public ResponseEntity<BulkDeleteRoutinesResponse> bulkDeleteRoutines(
      @PathVariable UUID memberId,
      @Valid @RequestBody BulkDeleteRoutinesRequest request) {
    LOGGER.info("DELETE /api/v1/users/members/{}/routines/bulk - Bulk delete routines request received", memberId);
    
    BulkDeleteRoutinesInput input = new BulkDeleteRoutinesInput(
        memberId,
        request.routineIds()
    );
    
    BulkDeleteRoutinesOutput output = bulkDeleteRoutinesUseCase.execute(input);
    
    BulkDeleteRoutinesResponse response = new BulkDeleteRoutinesResponse(
        output.deletedCount(),
        output.message()
    );
    
    LOGGER.info("DELETE /api/v1/users/members/{}/routines/bulk - {}", memberId, output.message());
    return ResponseEntity.ok(response);
  }
  
  @PutMapping("/members/{memberId}/routines/bulk")
  public ResponseEntity<BulkSyncRoutinesResponse> bulkSyncRoutines(
      @PathVariable UUID memberId,
      @Valid @RequestBody BulkSyncRoutinesRequest request) {
    LOGGER.info("PUT /api/v1/users/members/{}/routines/bulk - Bulk sync routines request received", memberId);
    
    BulkSyncRoutinesInput input = new BulkSyncRoutinesInput(
        memberId,
        request.routineIds(),
        request.expirationDate()
    );
    
    BulkSyncRoutinesOutput output = bulkSyncRoutinesUseCase.execute(input);
    
    BulkSyncRoutinesResponse response = new BulkSyncRoutinesResponse(
        output.addedCount(),
        output.removedCount(),
        output.unchangedCount(),
        output.message()
    );
    
    LOGGER.info("PUT /api/v1/users/members/{}/routines/bulk - {}", memberId, output.message());
    return ResponseEntity.ok(response);
  }
  
  private UserResponse toUserResponse(UserOutput output) {
    List<AssignedRoutineResponse> assignedRoutines = null;
    if (output.assignedRoutines() != null) {
      assignedRoutines = output.assignedRoutines().stream()
          .map(this::toAssignedRoutineResponse)
          .toList();
    }
    
    return new UserResponse(
        output.id(),
        output.name(),
        output.email(),
        output.role(),
        output.status(),
        output.coachId(),
        output.workoutPerWeek(),
        output.adherenceRate(),
        output.lastWorkoutDate(),
        assignedRoutines
    );
  }
  
  private AssignedRoutineResponse toAssignedRoutineResponse(AssignedRoutineOutput output) {
    return new AssignedRoutineResponse(
        output.id(),
        output.name(),
        output.expirationDate(),
        output.isExpired()
    );
  }
}
