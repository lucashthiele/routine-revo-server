package com.lucashthiele.routine_revo_server.infrastructure.data.user;

import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.RoleData;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.StatusData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserData {
  @Id
  private UUID id;
  
  @Column(nullable = false) 
  private String name;
  @Column(nullable = false, unique = true)
  private String email;
  @Column(name = "hashed_password", nullable = false)
  private String hashedPassword;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private StatusData status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private RoleData role;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;


}
