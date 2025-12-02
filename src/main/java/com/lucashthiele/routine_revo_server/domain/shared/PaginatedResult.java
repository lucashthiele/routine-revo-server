package com.lucashthiele.routine_revo_server.domain.shared;

import java.util.List;

public record PaginatedResult<T>(
    List<T> items,
    long total,
    int page,
    int size,
    int totalPages
) {
  public static <T> PaginatedResult<T> of(List<T> items, long total, int page, int size) {
    int totalPages = (int) Math.ceil((double) total / size);
    return new PaginatedResult<>(items, total, page, size, totalPages);
  }
}

