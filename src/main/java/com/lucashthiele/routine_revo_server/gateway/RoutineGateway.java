package com.lucashthiele.routine_revo_server.gateway;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineFilter;
import com.lucashthiele.routine_revo_server.domain.shared.PaginatedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoutineGateway {
  UUID save(Routine routine);
  
  Optional<Routine> findById(UUID id);
  
  void delete(UUID id);
  
  List<Routine> findAllByMemberId(UUID memberId);
  
  Routine update(Routine routine);
  
  PaginatedResult<Routine> findAll(RoutineFilter filter, int page, int size);
}

