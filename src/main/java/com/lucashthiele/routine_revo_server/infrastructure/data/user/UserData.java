package com.lucashthiele.routine_revo_server.infrastructure.data.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.RoleData;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.StatusData;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "coach_id",
      referencedColumnName = "id"
  )
  private UserData coach;
  @Column(name = "workout_per_week")
  private Integer workoutPerWeek;
  @Column(name = "adherence_rate")
  private Double adherenceRate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "user_status")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private StatusData status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "user_role")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private RoleData role;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public String toJson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    return ow.writeValueAsString(this);
  }
}
