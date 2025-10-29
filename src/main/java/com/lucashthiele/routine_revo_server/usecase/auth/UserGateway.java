package com.lucashthiele.routine_revo_server.usecase.auth;

import com.lucashthiele.routine_revo_server.domain.user.User;

import java.util.Optional;

public interface UserGateway {
  Optional<User> findByEmail(String email);
}
