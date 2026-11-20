/*package com.localy.backend.infrastructure.config;

import java.time.Instant;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.domain.enums.AuthProvider;
import com.localy.backend.domain.enums.Rol;
import com.localy.backend.domain.model.User;

@Component
@Profile("!tour-seed")
public class AdminInitializer implements CommandLineRunner{
    
    private final PasswordEncoder passwordEncoder;
    private final UserRepositoryPort userRepositoryPort; 
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(
            PasswordEncoder passwordEncoder,
            UserRepositoryPort userRepositoryPort,
            @Value("${localy.admin.email}") String adminEmail,
            @Value("${localy.admin.password}") String adminPassword) {
        this.passwordEncoder = passwordEncoder;
        this.userRepositoryPort = userRepositoryPort;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepositoryPort.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
            .name("admin")
            .lastName("localy")
            .email(adminEmail)
            .password(passwordEncoder.encode(adminPassword))
            .authProvider(AuthProvider.LOCAL)
            .roles(Set.of(Rol.ADMIN))
            .createdAt(Instant.now())
            .build();
            userRepositoryPort.save(admin);
        }
    }   
}*/