package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.localy.backend.domain.enums.Rol;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity,Long>{
       boolean existsByEmail(String email);
       Optional<UserEntity> findByEmail(String email);
       Page<UserEntity> findByRolesIn(Set<Rol> roles, Pageable pageable);
}