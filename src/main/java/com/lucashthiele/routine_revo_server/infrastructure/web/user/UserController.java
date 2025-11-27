package com.lucashthiele.routine_revo_server.infrastructure.web.user;

import com.lucashthiele.routine_revo_server.infrastructure.web.user.dto.CreateUserRequest;
import com.lucashthiele.routine_revo_server.usecase.user.CreateUserUseCase;
import com.lucashthiele.routine_revo_server.usecase.user.input.CreateUserInput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  private final CreateUserUseCase createUserUseCase;

  public UserController(CreateUserUseCase createUserUseCase) {
    this.createUserUseCase = createUserUseCase;
  }
  
  @PostMapping
  public ResponseEntity<Void> createUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                         @Valid @RequestBody CreateUserRequest request) {
    LOGGER.info("POST /api/v1/password-reset/new-password - Password update request received");

    CreateUserInput input = new CreateUserInput(request.name(),
        request.email(),
        request.role(),
        request.coachId());

    createUserUseCase.execute(input);

    LOGGER.info("POST /api/v1/password-reset/new-password - Password updated successfully");
    return ResponseEntity.ok().build();
  }
}
