package com.lucashthiele.routine_revo_server.infrastructure.data.user;

import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.RoleData;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.StatusData;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class UserDataMapper {
  
  public User toDomain(UserData userData) {
    if (userData == null) return null;
    
    return User.builder()
        .id(userData.getId())
        .name(userData.getName())
        .email(userData.getEmail())
        .password(userData.getHashedPassword())
        .role(Role.valueOf(userData.getRole().name()))
        .status(Status.valueOf(userData.getStatus().name()))
        .coachId(userData.getCoach() != null ? userData.getCoach().getId() : null)
        .workoutPerWeek(userData.getWorkoutPerWeek())
        .adherenceRate(userData.getAdherenceRate())
        .createdAt(userData.getCreatedAt() != null 
            ? userData.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant() 
            : null)
        .build();
  }
  
  public UserData toData(User user) {
    if (user == null) return null;
    
    if (user.getStatus() == null) {
      throw new IllegalArgumentException(
          "User status cannot be null. User: " + user.getEmail()
      );
    }
    
    if (user.getRole() == null) {
      throw new IllegalArgumentException(
          "User role cannot be null. User: " + user.getEmail()
      );
    }
    
    UserData userData = new UserData();
    userData.setId(user.getId());
    userData.setName(user.getName());
    userData.setEmail(user.getEmail());
    userData.setHashedPassword(user.getPassword());
    userData.setRole(RoleData.valueOf(user.getRole().name()));
    userData.setStatus(StatusData.valueOf(user.getStatus().name()));
    userData.setWorkoutPerWeek(user.getWorkoutPerWeek());
    userData.setAdherenceRate(user.getAdherenceRate());
    
    // Set coach relationship if coachId is present
    if (user.getCoachId() != null) {
      UserData coach = new UserData();
      coach.setId(user.getCoachId());
      userData.setCoach(coach);
    }
    
    return userData;
  }
}
