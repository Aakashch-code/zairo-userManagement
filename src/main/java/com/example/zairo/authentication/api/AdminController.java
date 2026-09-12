package com.example.zairo.authentication.api;

import com.example.zairo.authentication.application.dto.AdminCreateUserRequest;
import com.example.zairo.authentication.application.dto.AdminUpdateUserRequest;
import com.example.zairo.authentication.application.dto.UserResponse;
import com.example.zairo.authentication.application.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<String> createUser(
            @Valid @RequestBody AdminCreateUserRequest request) {

        return ResponseEntity.ok(adminService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUpdateUserRequest request) {

        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {

        return ResponseEntity.ok(adminService.deleteUser(id));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<String> assignRole(
            @PathVariable UUID id,
            @RequestParam String roleName) {

        return ResponseEntity.ok(adminService.assignRole(id, roleName));
    }
}