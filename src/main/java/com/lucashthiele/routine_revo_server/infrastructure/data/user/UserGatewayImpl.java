package com.lucashthiele.routine_revo_server.infrastructure.data.user;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.usecase.auth.UserGateway;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserGatewayImpl implements UserGateway {
  
  private final UserJpaRepository userRepository;
  private final UserDataMapper userDataMapper;

  public UserGatewayImpl(UserJpaRepository userRepository, UserDataMapper userDataMapper) {
    this.userRepository = userRepository;
    this.userDataMapper = userDataMapper;
  }

  @Override
  public Optional<User> findByEmail(String email) {
    Optional<UserData> userDataOptional = userRepository.findByEmail(email);
    
    return userDataOptional.map(userDataMapper::toDomain);
  }

  @Override
  public void updatePasswordByEmail(String email, String hashedPassword) {
      userRepository.updatePasswordByEmail(email, hashedPassword);
  }
}
