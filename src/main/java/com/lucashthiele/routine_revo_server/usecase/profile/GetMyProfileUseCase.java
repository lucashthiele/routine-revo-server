package com.lucashthiele.routine_revo_server.usecase.profile;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.profile.exception.ProfileNotFoundException;
import com.lucashthiele.routine_revo_server.usecase.profile.input.GetMyProfileInput;
import com.lucashthiele.routine_revo_server.usecase.profile.output.UserProfileOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GetMyProfileUseCase implements UseCaseInterface<UserProfileOutput, GetMyProfileInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GetMyProfileUseCase.class);
  
  private final UserGateway userGateway;

  public GetMyProfileUseCase(UserGateway userGateway) {
    this.userGateway = userGateway;
  }

  @Override
  public UserProfileOutput execute(GetMyProfileInput input) {
    LOGGER.info("[GetMyProfileUseCase] Fetching profile for user: {}", input.userId());
    
    User user = userGateway.findById(input.userId())
        .orElseThrow(() -> new ProfileNotFoundException(input.userId()));
    
    LOGGER.info("[GetMyProfileUseCase] Profile found for user: {}", input.userId());
    
    return UserProfileOutput.from(user);
  }
}

