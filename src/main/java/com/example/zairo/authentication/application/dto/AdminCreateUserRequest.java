package com.example.zairo.authentication.application.dto;

import com.example.zairo.authentication.domain.model.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.Set;

@Data
public class AdminCreateUserRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$",
            message = "Password must be strong (at least 8 characters, with uppercase, lowercase, number, and special character)")
    private String password;

    @NotEmpty(message = "At least one role must be assigned (e.g., ['USER', 'MANAGER'])")
    private Set<Roles> roles;
}