package com.lucashthiele.routine_revo_server.infrastructure.data.workout;

import com.lucashthiele.routine_revo_server.domain.workout.WorkoutSession;
import com.lucashthiele.routine_revo_server.gateway.WorkoutGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class WorkoutGatewayImpl implements WorkoutGateway {
  private static final Logger LOGGER = LoggerFactory.getLogger(WorkoutGatewayImpl.class);
  
  private final WorkoutJpaRepository workoutRepository;
  private final WorkoutDataMapper workoutDataMapper;
  
  public WorkoutGatewayImpl(WorkoutJpaRepository workoutRepository, WorkoutDataMapper workoutDataMapper) {
    this.workoutRepository = workoutRepository;
    this.workoutDataMapper = workoutDataMapper;
  }
  
  @Override
  @Transactional
  public UUID save(WorkoutSession session) {
    LOGGER.info("Saving workout session for member: {}", session.getMemberId());
    
    WorkoutSessionData data = workoutDataMapper.toData(session);
    
    WorkoutSessionData savedSession = workoutRepository.save(data);
    
    LOGGER.info("Workout session saved successfully with ID: {}", savedSession.getId());
    
    return savedSession.getId();
  }
  
  @Override
  @Transactional(readOnly = true)
  public Optional<WorkoutSession> findById(UUID id) {
    LOGGER.debug("Fetching workout session by ID: {}", id);
    
    Optional<WorkoutSessionData> data = workoutRepository.findByIdWithItems(id);
    
    if (data.isPresent()) {
      LOGGER.debug("Workout session found with ID: {}", id);
    } else {
      LOGGER.debug("Workout session not found with ID: {}", id);
    }
    
    return data.map(workoutDataMapper::toDomain);
  }
  
  @Override
  @Transactional(readOnly = true)
  public List<WorkoutSession> findByMemberId(UUID memberId) {
    LOGGER.debug("Fetching workout sessions for member ID: {}", memberId);
    
    List<WorkoutSessionData> dataList = workoutRepository.findAllByMemberId(memberId);
    
    List<WorkoutSession> sessions = dataList.stream()
        .map(workoutDataMapper::toDomain)
        .collect(Collectors.toList());
    
    LOGGER.debug("Found {} workout sessions for member ID: {}", sessions.size(), memberId);
    
    return sessions;
  }
  
  @Override
  @Transactional(readOnly = true)
  public long countWorkoutsByMemberSince(UUID memberId, Instant since) {
    LOGGER.debug("Counting workouts for member {} since {}", memberId, since);
    
    long count = workoutRepository.countByMemberIdAndStartedAtAfter(memberId, since);
    
    LOGGER.debug("Found {} workouts for member {} since {}", count, memberId, since);
    
    return count;
  }
}

