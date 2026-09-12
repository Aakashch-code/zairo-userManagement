package com.example.zairo.authentication.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TaskResponse {
    private UUID id;
    private String description;
}