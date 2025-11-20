package com.lucashthiele.routine_revo_server.infrastructure.notification.common;

public interface EmailSender {
  void send(String to, String subject, String bodyHtml);
}
