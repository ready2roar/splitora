package com.splitora.repository;

import com.splitora.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    // Custom queries for group lookups can be added here as needed
}
