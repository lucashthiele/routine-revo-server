package com.lucashthiele.routine_revo_server.infrastructure.notification.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
public class EmailTemplateService {
  private static final Logger LOGGER = LoggerFactory.getLogger(EmailTemplateService.class);
  
  private final SpringTemplateEngine templateEngine;
  
  @Value("${app.frontend.url}")
  private String frontendUrl;

  public EmailTemplateService(SpringTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }
  
  public String generatePasswordResetHtml(String username, String token) {
    LOGGER.debug("Generating password reset email template for user: {}", username);
    
    var firstName = this.extractFirstName(username);
    var link = String.format("%s/reset-password?token=%s", frontendUrl, token);

    Map<String, Object> variables = Map.of(
        "firstName", firstName,
        "resetLink", link
    );
    
    Context context = new Context();
    context.setVariables(variables);

    String html = templateEngine.process("password-reset", context);
    
    LOGGER.debug("Password reset email template generated successfully");
    return html;
  }

  public String generateOnboardingHtml(String username, String onboardingToken) {
    LOGGER.debug("Generating onboarding email template for user: {}", username);

    var firstName = this.extractFirstName(username);
    var link = String.format("%s/onboarding?token=%s", frontendUrl, onboardingToken);

    Map<String, Object> variables = Map.of(
        "firstName", firstName,
        "onboardingLink", link
    );

    Context context = new Context();
    context.setVariables(variables);

    String html = templateEngine.process("onboarding", context);

    LOGGER.debug("Onboarding email template generated successfully");
    return html;
  }
  
  private String extractFirstName(String username) {
    if (username.contains(" "))
      return username.split(" ")[0];

    return username;
  }
}
