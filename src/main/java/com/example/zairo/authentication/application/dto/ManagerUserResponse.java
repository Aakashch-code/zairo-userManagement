package com.example.zairo.authentication.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ManagerUserResponse {
    private UUID id;
    private String name;
    private String email;
    private Set<String> roles;
    private List<TaskResponse> tasks;
}