package com.localy.backend.application.usecase.auth;

import java.time.Instant;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.localy.backend.application.dto.request.RegisterUserRequest;
import com.localy.backend.application.dto.response.UserResponse;
import com.localy.backend.application.mapper.UserMapper;
import com.localy.backend.application.port.input.auth.RegisterUserUseCase;
import com.localy.backend.application.port.output.UserRepositoryPort;
import com.localy.backend.domain.enums.AuthProvider;
import com.localy.backend.domain.enums.Rol;
import com.localy.backend.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    /*
    Clase encargada de implementar la lógica de negocio para registrar un nuevo usuario en el sistema. 
    Esta clase implementa la interfaz RegisterUserUseCase y utiliza un UserRepositoryPort para interactuar con la capa de 
    persistencia y un UserMapper para mapear entre los objetos de dominio y los DTOs.
    */

    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse execute(RegisterUserRequest userRequest) {

        if(userRepositoryPort.existsByEmail(userRequest.getEmail())){
            throw new IllegalArgumentException("El correo electrónico ya está registrado"); 
        }

        User user = userMapper.toDomain(userRequest);

        User userToSave = user.toBuilder()
        .password(passwordEncoder.encode(userRequest.getPassword()))
        .roles(Set.of(Rol.USER))
        .authProvider(AuthProvider.LOCAL)
        .createdAt(Instant.now())
        .build();

        User saveUser = userRepositoryPort.save(userToSave);
        return userMapper.toResponse(saveUser);
    }
}