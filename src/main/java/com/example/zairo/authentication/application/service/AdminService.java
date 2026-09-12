package com.example.zairo.authentication.application.service;

import com.example.zairo.authentication.application.dto.AdminCreateUserRequest;
import com.example.zairo.authentication.application.dto.AdminUpdateUserRequest;
import com.example.zairo.authentication.application.dto.UserResponse;
import com.example.zairo.authentication.domain.exception.DuplicateUserException;
import com.example.zairo.authentication.domain.exception.RoleNotFoundException;
import com.example.zairo.authentication.domain.exception.UserNotFoundException;
import com.example.zairo.authentication.domain.model.Roles;
import com.example.zairo.authentication.domain.model.Role;
import com.example.zairo.authentication.domain.model.User;
import com.example.zairo.authentication.infrastructure.repository.RoleRepository;
import com.example.zairo.authentication.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public String createUser(AdminCreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("Email already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        for (Roles roleName : request.getRoles()) {

            Role role = roleRepository.findByName(roleName.name())
                    .orElseThrow(() ->
                            new RoleNotFoundException(
                                    "Role not found: " + roleName
                            ));

            user.getRoles().add(role);
        }

        userRepository.save(user);

        return "User created successfully";
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public String updateUser(UUID id, AdminUpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateUserException("Email already in use");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        userRepository.save(user);

        return "User updated successfully";
    }

    public String deleteUser(UUID id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }

        userRepository.deleteById(id);

        return "User deleted successfully";
    }

    public String assignRole(UUID id, String roleName) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Role role = roleRepository.findByName(
                        roleName.toUpperCase()
                )
                .orElseThrow(() ->
                        new RoleNotFoundException(
                                "Role not found: " + roleName
                        ));

        user.getRoles().add(role);

        userRepository.save(user);

        return "Role assigned successfully";
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