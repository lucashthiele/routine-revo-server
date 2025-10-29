package com.lucashthiele.routine_revo_server.infrastructure.data.user;

import com.lucashthiele.routine_revo_server.domain.user.Role;
import com.lucashthiele.routine_revo_server.domain.user.Status;
import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.RoleData;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.StatusData;
import org.springframework.stereotype.Component;

@Component
public class UserDataMapper {
  
  public User toDomain(UserData userData) {
    if (userData == null) return null;
    
    return new User(
        userData.getId(),
        userData.getName(),
        userData.getEmail(),
        userData.getHashedPassword(),
        Role.valueOf(userData.getRole().name()),
        Status.valueOf(userData.getStatus().name())
    );
  }
  
  public UserData toData(User user) {
    if (user == null) return null;
    
    UserData userData = new UserData();userData.setId(user.getId());
    userData.setName(user.getName());
    userData.setEmail(user.getEmail());
    userData.setHashedPassword(user.getPassword());
    userData.setRole(RoleData.valueOf(user.getRole().name()));
    userData.setStatus(StatusData.valueOf(user.getStatus().name()));
    
    return userData;
  }
}
