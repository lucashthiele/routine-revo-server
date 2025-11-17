package com.lucashthiele.routine_revo_server.infrastructure.data.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserData, UUID> {
  Optional<UserData> findByEmail(String email);

  @Modifying
  @Transactional
  @Query("UPDATE UserData u SET u.hashedPassword = :newHashedPassword WHERE u.email = :email")
  void updatePasswordByEmail(@Param("email") String email,
                             @Param("newHashedPassword") String newHashedPassword);
  
}
