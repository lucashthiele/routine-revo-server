package com.lucashthiele.routine_revo_server.infrastructure.security.tokenprovider;

public enum TokenProviderPurposeType {
  AUTHENTICATION("authentication"),
  RESET_PASSWORD("reset-password"),
  ONBOARDING("onboarding");
  
  private final String purposeValue;

  TokenProviderPurposeType(String purposeValue) {
    this.purposeValue = purposeValue;
  }
  
  public String value() {
    return purposeValue;
  }
}
