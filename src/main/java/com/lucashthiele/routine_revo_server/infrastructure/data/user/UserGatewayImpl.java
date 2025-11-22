package com.lucashthiele.routine_revo_server.infrastructure.data.user;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.StatusData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserGatewayImpl implements UserGateway {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserGatewayImpl.class);
  
  private final UserJpaRepository userRepository;
  private final UserDataMapper userDataMapper;

  public UserGatewayImpl(UserJpaRepository userRepository, UserDataMapper userDataMapper) {
    this.userRepository = userRepository;
    this.userDataMapper = userDataMapper;
  }

  @Override
  public Optional<User> findByEmail(String email) {
    LOGGER.debug("Fetching user from database by email: {}", email);
    Optional<UserData> userDataOptional = userRepository.findByEmail(email);
    
    if (userDataOptional.isPresent()) {
      LOGGER.debug("User found in database: {}", email);
    } else {
      LOGGER.debug("User not found in database: {}", email);
    }
    
    return userDataOptional.map(userDataMapper::toDomain);
  }

  @Override
  public void updatePasswordByEmail(String email, String hashedPassword) {
    LOGGER.info("Updating password for user: {}", email);
    userRepository.updatePasswordByEmail(email, hashedPassword);
    LOGGER.info("Password updated successfully for user: {}", email);
  }
  
  @Override
  public void activateUserAccountByEmail(String email, String hashedPassword) {
    LOGGER.info("Updating password and activating user: {}", email);
    userRepository.updatePasswordAndActivateByEmail(email, hashedPassword);
    LOGGER.info("Account updated successfully for user: {}", email);
  }

  @Override
  public Optional<User> findUserByEmailAndStatus(String email, StatusData status) {
    LOGGER.debug("Fetching user from database by email and status: {} - {}", email, status.name());
    Optional<UserData> userDataOptional = userRepository.findByEmailAndStatus(email, status);
    
    return userDataOptional.map(userDataMapper::toDomain);
  }
}
