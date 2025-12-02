package com.lucashthiele.routine_revo_server.usecase.user;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.user.exception.UserNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.user.input.GetUserByIdInput;
import com.lucashthiele.routine_revo_server.usecase.user.output.UserOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetUserByIdUseCase implements UseCaseInterface<UserOutput, GetUserByIdInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetUserByIdUseCase.class);
  
  private final UserGateway userGateway;

  public GetUserByIdUseCase(UserGateway userGateway) {
    this.userGateway = userGateway;
  }

  @Override
  public UserOutput execute(GetUserByIdInput input) {
    LOGGER.info("[GetUserByIdUseCase] Getting user with ID: {}", input.userId());
    
    User user = userGateway.findById(input.userId())
        .orElseThrow(() -> new UserNotFoundException(input.userId()));
    
    LOGGER.info("[GetUserByIdUseCase] User found: {}", input.userId());
    
    return UserOutput.from(user);
  }
}

