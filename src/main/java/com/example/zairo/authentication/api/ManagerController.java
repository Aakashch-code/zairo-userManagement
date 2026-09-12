package com.example.zairo.authentication.api;

import com.example.zairo.authentication.application.dto.ManagerUserResponse;
import com.example.zairo.authentication.application.dto.TaskRequest;
import com.example.zairo.authentication.application.service.ManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping("/users")
    public ResponseEntity<List<ManagerUserResponse>> viewAllUsers() {

        return ResponseEntity.ok(managerService.getAllUsers());
    }

    @PostMapping("/tasks")
    public ResponseEntity<String> assignTask(
            @Valid @RequestBody TaskRequest request) {

        return ResponseEntity.ok(
                managerService.assignTask(request)
        );
    }
}