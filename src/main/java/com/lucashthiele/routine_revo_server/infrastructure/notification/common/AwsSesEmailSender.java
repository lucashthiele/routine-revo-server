package com.lucashthiele.routine_revo_server.infrastructure.notification.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Component
public class AwsSesEmailSender implements EmailSender {
  private static final Logger LOGGER = LoggerFactory.getLogger(AwsSesEmailSender.class);
  private final SesClient sesClient;
  
  @Value("${spring.cloud.aws.ses.from}")
  private String fromEmail;

  public AwsSesEmailSender(SesClient sesClient) {
    this.sesClient = sesClient;
  }

  @Override
  @Async("emailExecutor")
  public void send(String to, String subject, String bodyHtml) {
    LOGGER.info("Starting async email sending to: {}", to);

    try {
      Destination destination = Destination.builder()
          .toAddresses(to)
          .build();
  
      Content subjectContent = Content.builder()
          .data(subject)
          .build();
  
      Content bodyContent = Content.builder()
          .data(bodyHtml)
          .build();
  
      Body body = Body.builder()
          .html(bodyContent)
          .build();
  
      Message message = Message.builder()
          .subject(subjectContent)
          .body(body)
          .build();
  
      SendEmailRequest request = SendEmailRequest.builder()
          .source(fromEmail)
          .destination(destination)
          .message(message)
          .build();

      sesClient.sendEmail(request);
      
      LOGGER.info("Email sent successfully to: {}", to);
    } catch (SesException e) {
      LOGGER.error("AWS SES Error sending email to: {}", to, e);
    } catch (Exception e) {
      LOGGER.error("Unexpected error sending email to: {}", to, e);
    }
  }
}
