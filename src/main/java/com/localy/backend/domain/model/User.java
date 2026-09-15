package com.localy.backend.domain.model;

import java.time.Instant;
import java.util.Set;

import com.localy.backend.domain.enums.AuthProvider;
import com.localy.backend.domain.enums.Rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

@Builder(toBuilder = true)
public class User {
    private Long id;
    private String profilePhoto;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private AuthProvider authProvider;
    private String providerId;
    private Set<Rol> roles;
    private Instant createdAt;
}