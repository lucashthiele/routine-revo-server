package com.lucashthiele.routine_revo_server.infrastructure.data.user;

import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.StatusData;
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

  Optional<UserData> findByEmailAndStatus(String email, StatusData status);
  
  @Modifying
  @Transactional
  @Query("""
      UPDATE UserData u\s
      SET u.hashedPassword = :hashedPassword,
          u.status = 'ACTIVE'\s
      WHERE u.email = :email""")
  void updatePasswordAndActivateByEmail(@Param("email") String email,
                                        @Param("hashedPassword") String hashedPassword);

  @Modifying
  @Transactional
  @Query(value = "UPDATE users SET coach_id = :coachId WHERE id = :userId", nativeQuery = true)
  void updateCoachIdById(@Param("coachId") UUID coachId,
                         @Param("userId") UUID userId);
}
