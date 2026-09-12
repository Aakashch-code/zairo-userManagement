package com.example.zairo.authentication.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TaskRequest {

    @NotBlank(message = "Task description is required")
    private String description;

    @NotNull(message = "Assigned user ID is required")
    private UUID assignedUserId;
}