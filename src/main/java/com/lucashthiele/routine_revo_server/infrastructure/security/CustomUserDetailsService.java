package com.lucashthiele.routine_revo_server.infrastructure.security;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.gateway.UserGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);
  
  private final UserGateway userGateway;
  
  public CustomUserDetailsService(UserGateway userGateway) {
    this.userGateway = userGateway;
  }
  
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    LOGGER.info("CustomUserDetailsService - Loading user by email: {}", email);
    
    User user = userGateway.findByEmail(email)
        .orElseThrow(() -> {
          LOGGER.error("CustomUserDetailsService - User not found with email: {}", email);
          return new UsernameNotFoundException("User not found with email: " + email);
        });
    
    LOGGER.info("CustomUserDetailsService - User found: id={}, role={}, status={}", 
        user.getId(), user.getRole(), user.getStatus());
    
    return new CustomUserDetails(
        user.getId(),
        user.getEmail(),
        user.getPassword(),
        user.getRole()
    );
  }
}

