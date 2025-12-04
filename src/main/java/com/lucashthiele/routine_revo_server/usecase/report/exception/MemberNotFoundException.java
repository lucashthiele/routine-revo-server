package com.lucashthiele.routine_revo_server.usecase.report.exception;

import java.util.UUID;

public class MemberNotFoundException extends RuntimeException {
  public MemberNotFoundException(UUID memberId) {
    super("Member not found with ID: " + memberId);
  }
}

