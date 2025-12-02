package com.lucashthiele.routine_revo_server.usecase.user;

import com.lucashthiele.routine_revo_server.domain.shared.PaginatedResult;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.domain.user.UserFilter;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.user.input.ListUsersInput;
import com.lucashthiele.routine_revo_server.usecase.user.output.ListUsersOutput;
import com.lucashthiele.routine_revo_server.usecase.user.output.UserOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListUsersUseCase implements UseCaseInterface<ListUsersOutput, ListUsersInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ListUsersUseCase.class);
  
  private final UserGateway userGateway;

  public ListUsersUseCase(UserGateway userGateway) {
    this.userGateway = userGateway;
  }

  @Override
  public ListUsersOutput execute(ListUsersInput input) {
    LOGGER.info("[ListUsersUseCase] Listing users with filter: {}, page: {}, size: {}", 
        input, input.page(), input.size());
    
    UserFilter filter = new UserFilter(input.name(), input.role(), input.status());
    
    PaginatedResult<User> result = userGateway.findAll(filter, input.page(), input.size());
    
    List<UserOutput> userOutputs = result.items().stream()
        .map(UserOutput::from)
        .toList();
    
    LOGGER.info("[ListUsersUseCase] Found {} users out of {} total", 
        userOutputs.size(), result.total());
    
    return new ListUsersOutput(
        userOutputs,
        result.total(),
        result.page(),
        result.size(),
        result.totalPages()
    );
  }
}

