package com.example.zairo.authentication.api;

import com.example.zairo.authentication.application.dto.TaskResponse;
import com.example.zairo.authentication.application.dto.UserResponse;
import com.example.zairo.authentication.application.service.UserService;
import com.example.zairo.authentication.domain.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity.ok(
                userService.getMyProfile(userId)
        );
    }

    @GetMapping("/me/tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks(
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity.ok(
                userService.getMyTasks(userId)
        );
    }
}