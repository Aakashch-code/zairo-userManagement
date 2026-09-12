package com.example.zairo.authentication.application.service;

import com.example.zairo.authentication.application.dto.TaskResponse;
import com.example.zairo.authentication.application.dto.UserResponse;
import com.example.zairo.authentication.domain.exception.UserNotFoundException;
import com.example.zairo.authentication.domain.model.Role;
import com.example.zairo.authentication.domain.model.Task;
import com.example.zairo.authentication.domain.model.User;
import com.example.zairo.authentication.infrastructure.repository.TaskRepository;
import com.example.zairo.authentication.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public UserResponse getMyProfile(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }

    public List<TaskResponse> getMyTasks(UUID userId) {
        return taskRepository.findByAssignedUserId(userId)
                .stream()
                .map(task -> new TaskResponse(task.getId(), task.getDescription()))
                .toList();
    }
}