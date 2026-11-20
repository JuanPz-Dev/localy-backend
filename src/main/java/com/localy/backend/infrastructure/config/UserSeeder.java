package com.localy.backend.infrastructure.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.domain.enums.AuthProvider;
import com.localy.backend.domain.enums.Rol;
import com.localy.backend.domain.model.User;

@Component
@Profile("mysql & !tour-seed")
public class UserSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UserSeeder.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepositoryPort userRepositoryPort;
    private final boolean enabled;

    public UserSeeder(
            PasswordEncoder passwordEncoder,
            UserRepositoryPort userRepositoryPort,
            @Value("${localy.seed.users.enabled:false}") boolean enabled) {
        this.passwordEncoder = passwordEncoder;
        this.userRepositoryPort = userRepositoryPort;
        this.enabled = enabled;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!enabled) {
            return;
        }

        int created = 0;
        int skipped = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("seed/usuarios_1200.csv").getInputStream(),
                StandardCharsets.UTF_8))) {

            String line = reader.readLine(); // encabezado
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] c = line.replace("\uFEFF", "").split(",", -1);
                String name = c[0].trim();
                String lastName = c[1].trim();
                String email = c[2].trim();
                String rawPassword = c[3].trim();

                if (userRepositoryPort.findByEmail(email).isPresent()) {
                    skipped++;
                    continue;
                }

                User user = User.builder()
                        .name(name)
                        .lastName(lastName)
                        .email(email)
                        .password(passwordEncoder.encode(rawPassword))
                        .authProvider(AuthProvider.LOCAL)
                        .roles(Set.of(Rol.USER))
                        .createdAt(Instant.now())
                        .build();
                userRepositoryPort.save(user);
                created++;
            }
        }

        log.info("UserSeeder: {} usuarios creados, {} ya existían", created, skipped);
    }
}
