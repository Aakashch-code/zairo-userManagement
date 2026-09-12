package com.example.zairo.authentication.application.service;

import com.example.zairo.authentication.application.dto.ManagerUserResponse;
import com.example.zairo.authentication.application.dto.TaskRequest;
import com.example.zairo.authentication.application.dto.TaskResponse;
import com.example.zairo.authentication.application.dto.UserResponse;
import com.example.zairo.authentication.domain.exception.InvalidTaskAssignmentException;
import com.example.zairo.authentication.domain.exception.UserNotFoundException;
import com.example.zairo.authentication.domain.model.Role;
import com.example.zairo.authentication.domain.model.Task;
import com.example.zairo.authentication.domain.model.User;
import com.example.zairo.authentication.infrastructure.repository.TaskRepository;
import com.example.zairo.authentication.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public List<ManagerUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toManagerResponse)
                .toList();
    }

    private ManagerUserResponse toManagerResponse(User user) {
        return new ManagerUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                user.getTasks()
                        .stream()
                        .map(task -> new TaskResponse(task.getId(), task.getDescription()))
                        .toList()
        );
    }

    public String assignTask(TaskRequest request) {

        User assignedUser = userRepository
                .findById(request.getAssignedUserId())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        boolean hasUserRole = assignedUser.getRoles()
                .stream()
                .anyMatch(role ->
                        role.getName().equalsIgnoreCase("USER"));

        if (!hasUserRole) {

            throw new InvalidTaskAssignmentException(
                    "Tasks can only be assigned to users with USER role"
            );
        }

        Task task = new Task();
        task.setDescription(request.getDescription());
        task.setAssignedUser(assignedUser);

        taskRepository.save(task);

        return "Task assigned successfully";
    }

    private UserResponse toResponse(User user) {

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
}