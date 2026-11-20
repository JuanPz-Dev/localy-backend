package com.localy.backend.infrastructure.adapter.output.persistence.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.domain.common.PageResult;
import com.localy.backend.domain.common.PaginationRequest;
import com.localy.backend.domain.enums.Rol;
import com.localy.backend.domain.model.User;
import com.localy.backend.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.localy.backend.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort{

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public User save(User user) {
        UserEntity entity = userPersistenceMapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return userPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
        .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
        .map(userPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public PageResult<User> findByRole(Rol roles, PaginationRequest pageRequest) {
        Pageable pageable = PageRequest.of(
            pageRequest.getPage(),
            pageRequest.getSize(),
            Sort.by(Sort.Direction.ASC, "id")
        );
        Page<UserEntity> page = userJpaRepository.findByRolesIn(Set.of(roles), pageable);
        return new PageResult<>(
            page.getContent().stream().map(userPersistenceMapper::toDomain).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }
}