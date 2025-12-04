package com.lucashthiele.routine_revo_server.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityContextHelper {
  
  /**
   * Extracts the current authenticated user's ID from the SecurityContext.
   * 
   * @return The UUID of the authenticated user
   * @throws IllegalStateException if no authentication is present or principal is invalid
   */
  public UUID getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("No authenticated user found in SecurityContext");
    }
    
    Object principal = authentication.getPrincipal();
    
    if (principal instanceof CustomUserDetails userDetails) {
      return userDetails.getId();
    }
    
    throw new IllegalStateException("Invalid principal type in SecurityContext: " + principal.getClass().getName());
  }
  
  /**
   * Extracts the current authenticated user's details from the SecurityContext.
   * 
   * @return The CustomUserDetails of the authenticated user
   * @throws IllegalStateException if no authentication is present or principal is invalid
   */
  public CustomUserDetails getCurrentUserDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("No authenticated user found in SecurityContext");
    }
    
    Object principal = authentication.getPrincipal();
    
    if (principal instanceof CustomUserDetails userDetails) {
      return userDetails;
    }
    
    throw new IllegalStateException("Invalid principal type in SecurityContext: " + principal.getClass().getName());
  }
}

