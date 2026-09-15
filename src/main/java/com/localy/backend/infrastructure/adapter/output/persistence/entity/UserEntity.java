package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.localy.backend.domain.enums.AuthProvider;
import com.localy.backend.domain.enums.Rol;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String profilePhoto;
    private String name;
    private String lastName;
    private String email;
    private String password;
    
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;
    private String providerId;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Rol> roles;
    private Instant createdAt;

    @OneToOne(mappedBy = "user")
    private GuideEntity guideEntity;

    @OneToMany(mappedBy = "user")
    private List<ReservationEntity> reservations =  new ArrayList<>();
}