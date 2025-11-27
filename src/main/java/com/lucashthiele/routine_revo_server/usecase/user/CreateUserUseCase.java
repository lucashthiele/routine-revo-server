package com.lucashthiele.routine_revo_server.usecase.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.user.exception.JsonProcessingRuntimeException;
import com.lucashthiele.routine_revo_server.usecase.user.input.CreateUserInput;
import com.lucashthiele.routine_revo_server.usecase.user.input.LinkCoachInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateUserUseCase implements UseCaseInterface<UUID, CreateUserInput> {
  private final UserGateway userGateway;
  private final LinkCoachUseCase linkCoachUseCase;
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserUseCase.class);


  public CreateUserUseCase(UserGateway userGateway, LinkCoachUseCase linkCoachUseCase) {
    this.userGateway = userGateway;
    this.linkCoachUseCase = linkCoachUseCase;
  }
  
  @Override
  public UUID execute(CreateUserInput input) {
    LOGGER.info("[CreateUserUseCase] Creating user. Input: {}", input);

    User user = User.builder()
        .name(input.name())
        .email(input.email())
        .role(input.role())
        .status(Status.PENDING)
        .build();

    try {
      UUID userId = userGateway.createUser(user);
      
      if (input.coachId() != null) {
        LinkCoachInput linkCoachInput = new LinkCoachInput(input.coachId(), userId);
        linkCoachUseCase.execute(linkCoachInput);
      }
      
      LOGGER.info("[CreateUserUseCase] User created: {}", userId);

      return userId;
    } catch (JsonProcessingException e) {
      LOGGER.info("[CreateUserUseCase] Not able to encode the user to json: {}", user, e);

      throw new JsonProcessingRuntimeException(e.getMessage());
    }
  }
}
