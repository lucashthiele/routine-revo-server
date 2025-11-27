package com.lucashthiele.routine_revo_server.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.UserData;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.StatusData;

import java.util.Optional;
import java.util.UUID;

public interface UserGateway {
  Optional<User> findByEmail(String email);
  void updatePasswordByEmail(String email, String hashedPassword);
  void activateUserAccountByEmail(String email, String hashedPassword);
  Optional<User> findUserByEmailAndStatus(String email, StatusData status);
  UUID createUser(User user) throws JsonProcessingException;
  void linkCoach(UUID coachId, UUID userId);
}
