package com.example.zairo.authentication.infrastructure.config;

import com.example.zairo.authentication.domain.model.Role;
import com.example.zairo.authentication.domain.model.User;
import com.example.zairo.authentication.infrastructure.repository.RoleRepository;
import com.example.zairo.authentication.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Seed Roles if they don't exist
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, "ADMIN"));
            roleRepository.save(new Role(null, "MANAGER"));
            roleRepository.save(new Role(null, "USER"));
            System.out.println("Default Roles Seeded!");
        }

        // 2. Seed Default Admin User
        if (!userRepository.existsByEmail("admin@test.com")) {
            User admin = new User();
            admin.setName("Super Admin");
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("Admin@123")); // Secure password

            Role adminRole = roleRepository.findByName("ADMIN").get();
            admin.getRoles().add(adminRole);

            userRepository.save(admin);
            System.out.println("Default Admin Seeded! (admin@test.com / Admin@123)");
        }
    }
}