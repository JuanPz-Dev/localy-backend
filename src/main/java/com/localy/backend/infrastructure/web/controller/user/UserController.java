package com.localy.backend.infrastructure.web.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localy.backend.application.dto.request.UpdateUserRequest;
import com.localy.backend.application.dto.response.UserResponse;
import com.localy.backend.application.port.input.user.UpdateUserProfileUseCase;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

@RestController 
@RequestMapping("/users")
@RequiredArgsConstructor 
public class UserController {
    
    private final UpdateUserProfileUseCase updateUserUseCase;

    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UpdateUserRequest updateUser) {
        UserResponse updatedUser = updateUserUseCase.execute(updateUser);
        return ResponseEntity.ok(updatedUser);
    }
}