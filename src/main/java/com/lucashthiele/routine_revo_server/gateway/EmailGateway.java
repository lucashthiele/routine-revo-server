package com.lucashthiele.routine_revo_server.gateway;

import com.lucashthiele.routine_revo_server.domain.user.User;

public interface EmailGateway {
  void sendPasswordResetEmail(User user, String resetToken);
  void sendOnboardingEmail(User user, String onboardingToken);
}
