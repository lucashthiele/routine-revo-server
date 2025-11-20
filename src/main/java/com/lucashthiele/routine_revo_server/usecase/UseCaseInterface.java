package com.lucashthiele.routine_revo_server.usecase;

public interface UseCaseInterface<R, I> {
  R execute(I input);
}
