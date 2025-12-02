package com.lucashthiele.routine_revo_server.infrastructure.web.user;

import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.infrastructure.web.user.dto.*;
import com.lucashthiele.routine_revo_server.usecase.user.CreateUserUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.GetUserByIdUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.InactivateUserUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.LinkCoachUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.ListUsersUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.UpdateUserUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.input.CreateUserInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.GetUserByIdInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.InactivateUserInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.LinkCoachInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.ListUsersInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.UpdateUserInput;
import com.lucashthiele.routine_revo_server.usecase.user.output.ListUsersOutput;
import com.lucashthiele.routine_revo_server.usecase.user.output.UserOutput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  public UserController(CreateUserUseCase createUserUseCase,
                        ListUsersUseCase listUsersUseCase,
                        GetUserByIdUseCase getUserByIdUseCase,
                        UpdateUserUseCase updateUserUseCase,
                        InactivateUserUseCase inactivateUserUseCase,
                        LinkCoachUseCase linkCoachUseCase) {
    this.createUserUseCase = createUserUseCase;
    this.listUsersUseCase = listUsersUseCase;
    this.getUserByIdUseCase = getUserByIdUseCase;
    this.updateUserUseCase = updateUserUseCase;
    this.inactivateUserUseCase = inactivateUserUseCase;
    this.linkCoachUseCase = linkCoachUseCase;
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
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size) {
    
    LOGGER.info("GET /api/v1/users - List users request received");
    
    ListUsersRequest request = new ListUsersRequest(name, role, status, page, size);
    
    ListUsersInput input = new ListUsersInput(
        request.name(),
        request.role(),
        request.status(),
        request.getPage(),
        request.getSize()
    );
    
    ListUsersOutput output = listUsersUseCase.execute(input);
    
    ListUsersResponse response = new ListUsersResponse(
        output.users().stream()
            .map(u -> new UserResponse(u.id(), u.name(), u.email(), u.role(), u.status(), u.coachId(), u.workoutPerWeek()))
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
    
    UserResponse response = new UserResponse(
        output.id(),
        output.name(),
        output.email(),
        output.role(),
        output.status(),
        output.coachId(),
        output.workoutPerWeek()
    );
    
    LOGGER.info("GET /api/v1/users/{} - User found and returned", id);
    return ResponseEntity.ok(response);
  }
  
  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateUserRequest request) {
    LOGGER.info("PUT /api/v1/users/{} - Update user request received", id);
    
    UpdateUserInput input = new UpdateUserInput(id, request.name(), request.email());
    
    UserOutput output = updateUserUseCase.execute(input);
    
    UserResponse response = new UserResponse(
        output.id(),
        output.name(),
        output.email(),
        output.role(),
        output.status(),
        output.coachId(),
        output.workoutPerWeek()
    );
    
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
}
