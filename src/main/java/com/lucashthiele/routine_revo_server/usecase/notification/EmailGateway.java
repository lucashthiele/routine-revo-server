package com.lucashthiele.routine_revo_server.usecase.notification;

import com.lucashthiele.routine_revo_server.domain.user.User;

public interface EmailGateway {
  void sendPasswordResetEmail(User user, String resetToken);
}
