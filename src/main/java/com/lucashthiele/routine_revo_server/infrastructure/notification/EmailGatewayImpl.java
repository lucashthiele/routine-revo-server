package com.lucashthiele.routine_revo_server.infrastructure.notification;

import com.lucashthiele.routine_revo_server.domain.user.User;
import com.lucashthiele.routine_revo_server.infrastructure.notification.common.EmailSender;
import com.lucashthiele.routine_revo_server.infrastructure.notification.template.EmailTemplateService;
import com.lucashthiele.routine_revo_server.usecase.notification.EmailGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailGatewayImpl implements EmailGateway {
  private static final Logger LOGGER = LoggerFactory.getLogger(EmailGatewayImpl.class);
  
  private final EmailSender emailSender;
  private final EmailTemplateService emailTemplateService;

  public EmailGatewayImpl(EmailSender emailSender, EmailTemplateService emailTemplateService) {
    this.emailSender = emailSender;
    this.emailTemplateService = emailTemplateService;
  }

  @Override
  public void sendPasswordResetEmail(User user, String resetToken) {
    LOGGER.info("Preparing password reset email for user: {}", user.getEmail());
    
    String subject = "Routine Revo - Restauração de Senha";
    
    String htmlBody = emailTemplateService.generatePasswordResetHtml(user.getName(), resetToken);
    
    emailSender.send(user.getEmail(), subject, htmlBody);
    
    LOGGER.info("Password reset email queued for user: {}", user.getEmail());
  }
}
