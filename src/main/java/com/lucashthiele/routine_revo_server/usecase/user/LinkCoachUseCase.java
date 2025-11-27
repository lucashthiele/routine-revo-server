package com.lucashthiele.routine_revo_server.usecase.user;

import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.user.input.LinkCoachInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LinkCoachUseCase implements UseCaseInterface<Void, LinkCoachInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(LinkCoachUseCase.class);
  
  private final UserGateway userGateway;

  public LinkCoachUseCase(UserGateway userGateway) {
    this.userGateway = userGateway;
  }

  @Override
  public Void execute(LinkCoachInput input) {
    LOGGER.info("[LinkCoachUseCase] Linking coach to user");
    userGateway.linkCoach(input.coachId(), input.userId());
    LOGGER.info("[LinkCoachUseCase] Coach linked to user");
    
    return null;
  }
}
