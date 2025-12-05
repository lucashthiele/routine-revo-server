package com.lucashthiele.routine_revo_server.infrastructure.data.routine;

import com.lucashthiele.routine_revo_server.domain.routine.Routine;
import com.lucashthiele.routine_revo_server.domain.routine.RoutineFilter;
import com.lucashthiele.routine_revo_server.domain.shared.PaginatedResult;
import com.lucashthiele.routine_revo_server.gateway.RoutineGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RoutineGatewayImpl implements RoutineGateway {
  private static final Logger LOGGER = LoggerFactory.getLogger(RoutineGatewayImpl.class);
  
  private final RoutineJpaRepository routineRepository;
  private final RoutineDataMapper routineDataMapper;
  
  public RoutineGatewayImpl(RoutineJpaRepository routineRepository, RoutineDataMapper routineDataMapper) {
    this.routineRepository = routineRepository;
    this.routineDataMapper = routineDataMapper;
  }
  
  @Override
  @Transactional
  public UUID save(Routine routine) {
    LOGGER.info("Creating routine: {}", routine.getName());
    
    RoutineData routineData = routineDataMapper.toData(routine);
    
    // Generate UUID if not already set
    if (routineData.getId() == null) {
      routineData.setId(UUID.randomUUID());
      LOGGER.debug("Generated new UUID for routine: {}", routineData.getId());
    }
    
    RoutineData savedRoutine = routineRepository.save(routineData);
    LOGGER.info("Routine created successfully with ID: {}", savedRoutine.getId());
    
    return savedRoutine.getId();
  }
  
  @Override
  @Transactional(readOnly = true)
  public Optional<Routine> findById(UUID id) {
    LOGGER.debug("Fetching routine from database by ID: {}", id);
    
    Optional<RoutineData> routineDataOptional = routineRepository.findByIdWithItems(id);
    
    if (routineDataOptional.isPresent()) {
      LOGGER.debug("Routine found in database with ID: {}", id);
    } else {
      LOGGER.debug("Routine not found in database with ID: {}", id);
    }
    
    return routineDataOptional.map(routineDataMapper::toDomain);
  }
  
  @Override
  @Transactional
  public void delete(UUID id) {
    LOGGER.info("Deleting routine with ID: {}", id);
    routineRepository.deleteById(id);
    LOGGER.info("Routine deleted successfully with ID: {}", id);
  }
  
  @Override
  @Transactional(readOnly = true)
  public List<Routine> findAllByMemberId(UUID memberId) {
    LOGGER.debug("Fetching routines for member ID: {}", memberId);
    
    List<RoutineData> routineDataList = routineRepository.findAllByMemberId(memberId);
    
    List<Routine> routines = routineDataList.stream()
        .map(routineDataMapper::toDomain)
        .collect(Collectors.toList());
    
    LOGGER.debug("Found {} routines for member ID: {}", routines.size(), memberId);
    
    return routines;
  }
  
  @Override
  @Transactional
  public Routine update(Routine routine) {
    LOGGER.info("Updating routine with ID: {}", routine.getId());
    
    // Fetch existing routine
    RoutineData existingRoutine = routineRepository.findByIdWithItems(routine.getId())
        .orElseThrow(() -> new IllegalArgumentException("Routine not found with ID: " + routine.getId()));
    
    // Clear existing items
    existingRoutine.clearItems();
    
    // Update fields
    existingRoutine.setName(routine.getName());
    existingRoutine.setDescription(routine.getDescription());
    existingRoutine.setExpirationDate(routine.getExpirationDate());
    
    // Update member if changed
    if (routine.getMemberId() != null) {
      if (existingRoutine.getMember() == null || !existingRoutine.getMember().getId().equals(routine.getMemberId())) {
        com.lucashthiele.routine_revo_server.infrastructure.data.user.UserData member = 
            new com.lucashthiele.routine_revo_server.infrastructure.data.user.UserData();
        member.setId(routine.getMemberId());
        existingRoutine.setMember(member);
      }
    } else {
      existingRoutine.setMember(null);
    }
    
    // Add new items
    if (routine.getItems() != null) {
      routine.getItems().forEach(item -> {
        RoutineItemData itemData = new RoutineItemData();
        itemData.setId(item.getId() != null ? item.getId() : UUID.randomUUID());
        
        com.lucashthiele.routine_revo_server.infrastructure.data.exercise.ExerciseData exercise = 
            new com.lucashthiele.routine_revo_server.infrastructure.data.exercise.ExerciseData();
        exercise.setId(item.getExerciseId());
        itemData.setExercise(exercise);
        
        itemData.setSets(item.getSets());
        itemData.setReps(item.getReps());
        itemData.setLoad(item.getLoad());
        itemData.setRestTime(item.getRestTime());
        itemData.setSequenceOrder(item.getSequenceOrder());
        
        existingRoutine.addItem(itemData);
      });
    }
    
    RoutineData updatedRoutineData = routineRepository.save(existingRoutine);
    LOGGER.info("Routine updated successfully with ID: {}", updatedRoutineData.getId());
    
    return routineDataMapper.toDomain(updatedRoutineData);
  }
  
  @Override
  @Transactional(readOnly = true)
  public PaginatedResult<Routine> findAll(RoutineFilter filter, int page, int size) {
    LOGGER.debug("Fetching routines with filter - creatorId: {}, memberId: {}, isExpired: {}, templatesOnly: {}, page: {}, size: {}",
        filter.creatorId(), filter.memberId(), filter.isExpired(), filter.templatesOnly(), page, size);
    
    boolean checkExpired = filter.isExpired() != null;
    boolean isExpired = filter.isExpired() != null && filter.isExpired();
    boolean templatesOnly = filter.templatesOnly() != null && filter.templatesOnly();
    
    Page<RoutineData> routinePage = routineRepository.findAllWithFilters(
        filter.creatorId(),
        filter.memberId(),
        templatesOnly,
        checkExpired,
        isExpired,
        Instant.now(),
        PageRequest.of(page, size)
    );
    
    List<Routine> routines = routinePage.getContent().stream()
        .map(routineDataMapper::toDomain)
        .collect(Collectors.toList());
    
    LOGGER.debug("Found {} routines out of {} total", routines.size(), routinePage.getTotalElements());
    
    return new PaginatedResult<>(
        routines,
        routinePage.getTotalElements(),
        routinePage.getNumber(),
        routinePage.getSize(),
        routinePage.getTotalPages()
    );
  }
}

