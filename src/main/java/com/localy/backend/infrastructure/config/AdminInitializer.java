package com.localy.backend.infrastructure.config;

import java.time.Instant;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.domain.enums.AuthProvider;
import com.localy.backend.domain.enums.Rol;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner{
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepositoryPort userRepositoryPort; 

    @Override
    public void run(String... args) throws Exception {

        String email = "adminLocaly@gmail.com";

        if (userRepositoryPort.findByEmail(email).isEmpty()) {
            User admin = User.builder()
            .name("admin")
            .lastName("localy")
            .email(email)
            .password(passwordEncoder.encode("Admin123"))
            .authProvider(AuthProvider.LOCAL)
            .roles(Set.of(Rol.ADMIN))
            .createdAt(Instant.now())
            .build();
            userRepositoryPort.save(admin);
        }
    }   
}