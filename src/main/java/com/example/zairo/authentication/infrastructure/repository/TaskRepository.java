package com.example.zairo.authentication.infrastructure.repository;

import com.example.zairo.authentication.domain.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByAssignedUserId(UUID userId);
}