package com.lucashthiele.routine_revo_server.usecase.user;

import com.lucashthiele.routine_revo_server.domain.shared.PaginatedResult;
import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.domain.user.UserFilter;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.usecase.UseCaseInterface;
import com.lucashthiele.routine_revo_server.usecase.user.input.SearchMembersInput;
import com.lucashthiele.routine_revo_server.usecase.user.output.ListUsersOutput;
import com.lucashthiele.routine_revo_server.usecase.user.output.UserOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchMembersUseCase implements UseCaseInterface<ListUsersOutput, SearchMembersInput> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SearchMembersUseCase.class);
  
  private final UserGateway userGateway;
  
  public SearchMembersUseCase(UserGateway userGateway) {
    this.userGateway = userGateway;
  }
  
  @Override
  public ListUsersOutput execute(SearchMembersInput input) {
    LOGGER.info("[SearchMembersUseCase] Searching for active members with filter: {}", input.name());
    
    // Create filter for MEMBER role and ACTIVE status
    UserFilter filter = new UserFilter(
        input.name(),
        Role.MEMBER,
        Status.ACTIVE
    );
    
    int page = input.page() != null ? input.page() : 0;
    int size = input.size() != null ? input.size() : 20;
    
    // Fetch users with filter
    PaginatedResult<User> paginatedUsers = userGateway.findAll(filter, page, size);
    
    // Convert to output
    List<UserOutput> userOutputs = paginatedUsers.items().stream()
        .map(user -> new UserOutput(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getStatus(),
            user.getCoachId(),
            user.getWorkoutPerWeek()
        ))
        .collect(Collectors.toList());
    
    LOGGER.info("[SearchMembersUseCase] Found {} active members", userOutputs.size());
    
    return new ListUsersOutput(
        userOutputs,
        paginatedUsers.total(),
        paginatedUsers.page(),
        paginatedUsers.size(),
        paginatedUsers.totalPages()
    );
  }
}

