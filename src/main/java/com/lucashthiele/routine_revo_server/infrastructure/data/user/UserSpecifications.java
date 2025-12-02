package com.lucashthiele.routine_revo_server.infrastructure.data.user;

import com.lucashthiele.routine_revo_server.domain.user.UserFilter;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.RoleData;
import com.lucashthiele.routine_revo_server.infrastructure.data.user.enums.StatusData;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecifications {
  
  private UserSpecifications() {
    // Utility class
  }
  
  public static Specification<UserData> withFilter(UserFilter filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      
      if (filter.name() != null && !filter.name().isBlank()) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + filter.name().toLowerCase() + "%"
            )
        );
      }
      
      if (filter.role() != null) {
        predicates.add(
            criteriaBuilder.equal(
                root.get("role"),
                RoleData.valueOf(filter.role().name())
            )
        );
      }
      
      if (filter.status() != null) {
        predicates.add(
            criteriaBuilder.equal(
                root.get("status"),
                StatusData.valueOf(filter.status().name())
            )
        );
      }
      
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}

