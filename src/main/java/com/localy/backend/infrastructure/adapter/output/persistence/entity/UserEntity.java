package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.localy.backend.domain.enums.AuthProvider;
import com.localy.backend.domain.enums.Rol;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "users" , 
       uniqueConstraints = {@UniqueConstraint(name = "uk_users_email",columnNames = "email")},
       indexes = {@Index(name = "idx_users_created_at", columnList = "created_at")}
    )
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String profilePhoto;

    @Column(nullable = false,length = 100)
    private String name;

    @Column(nullable = false,length = 100)
    private String lastName;

    @Column(nullable = false , unique = true , length = 180)
    private String email;

    @Column(length = 250)
    private String password;
    
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;
    private String providerId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
    uniqueConstraints = {@UniqueConstraint(name = "uk_user_roles",columnNames = {"user_id", "role"})})
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Set<Rol> roles;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private GuideEntity guideEntity;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ReservationEntity> reservations =  new ArrayList<>();

    @PrePersist
    protected void onCreate(){
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    } 
}