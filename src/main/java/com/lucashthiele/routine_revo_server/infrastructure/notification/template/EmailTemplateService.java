package com.lucashthiele.routine_revo_server.infrastructure.notification.template;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
public class EmailTemplateService {
  
  private final SpringTemplateEngine templateEngine;
  
  @Value("${app.frontend.url}")
  private String frontendUrl;

  public EmailTemplateService(SpringTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }
  
  public String generatePasswordResetHtml(String username, String token) {
    var firstName = this.extractFirstName(username);
    var link = String.format("%s/reset-password?token=%s", frontendUrl, token);

    Map<String, Object> variables = Map.of(
        "firstName", firstName,
        "resetLink", link
    );
    
    Context context = new Context();
    context.setVariables(variables);

    return templateEngine.process("password-reset", context);
  }

  private String extractFirstName(String username) {
    if (username.contains(" "))
      return username.split(" ")[0];
  
    return username;
  }
}
