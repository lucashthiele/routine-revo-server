package com.lucashthiele.routine_revo_server.infrastructure.data.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lucashthiele.routine_revo_server.domain.shared.PaginatedResult;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.domain.user.UserFilter;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.StatusData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

  @Override
  public UUID createUser(User user) throws JsonProcessingException {
    UserData userData = userDataMapper.toData(user);
    
    // Generate UUID if not already set
    if (userData.getId() == null) {
      userData.setId(UUID.randomUUID());
      LOGGER.debug("Generated new UUID for user: {}", userData.getId());
    }
    
    LOGGER.info("Creating user with email: {} and role: {}", userData.getEmail(), userData.getRole());
    UserData createdUser = userRepository.save(userData);
    LOGGER.info("User created successfully with ID: {}", createdUser.getId());
    
    return createdUser.getId();
  }

  @Override
  public void linkCoach(UUID coachId, UUID userId) {
    LOGGER.info("Linking coach ({}) to user ({})", coachId, userId);
    userRepository.updateCoachIdById(coachId, userId);
    LOGGER.info("Coach linked to user successfully");
  }

  @Override
  public PaginatedResult<User> findAll(UserFilter filter, int page, int size) {
    LOGGER.debug("Fetching users with filter: {}, page: {}, size: {}", filter, page, size);
    
    Specification<UserData> spec = UserSpecifications.withFilter(filter);
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    
    Page<UserData> userDataPage = userRepository.findAll(spec, pageable);
    
    List<User> users = userDataPage.getContent().stream()
        .map(userDataMapper::toDomain)
        .toList();
    
    LOGGER.debug("Found {} users out of {} total", users.size(), userDataPage.getTotalElements());
    
    return PaginatedResult.of(users, userDataPage.getTotalElements(), page, size);
  }

  @Override
  public Optional<User> findById(UUID id) {
    LOGGER.debug("Fetching user from database by ID: {}", id);
    Optional<UserData> userDataOptional = userRepository.findById(id);
    
    if (userDataOptional.isPresent()) {
      LOGGER.debug("User found in database with ID: {}", id);
    } else {
      LOGGER.debug("User not found in database with ID: {}", id);
    }
    
    return userDataOptional.map(userDataMapper::toDomain);
  }

  @Override
  public User update(User user) {
    LOGGER.info("Updating user with ID: {}", user.getId());
    UserData userData = userDataMapper.toData(user);
    UserData updatedUserData = userRepository.save(userData);
    LOGGER.info("User updated successfully with ID: {}", updatedUserData.getId());
    
    return userDataMapper.toDomain(updatedUserData);
  }
}
