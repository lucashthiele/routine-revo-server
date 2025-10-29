package com.lucashthiele.routine_revo_server.infrastructure.web.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email must be a valid email format")
  private String email;
  
  @NotBlank(message = "Password cannot be blank")
  private String password;
}
