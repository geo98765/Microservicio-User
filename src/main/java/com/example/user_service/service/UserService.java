package com.example.user_service.service;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.UpdatePasswordRequest;
import com.example.user_service.dto.UpdateProfileRequest;
import com.example.user_service.dto.UserRequest;
import com.example.user_service.dto.UserResponse;

public interface UserService {
    UserResponse registerUser(UserRequest request);

    UserResponse login(LoginRequest request);

    void logout(Integer userId);

    UserResponse changePassword(Integer userId, UpdatePasswordRequest request);

    UserResponse updateProfile(Integer userId, UpdateProfileRequest request);

    UserResponse getUserById(Integer userId);

    /**
     * Verifica si un usuario existe en el sistema
     * Usado por otros microservicios
     */
    boolean userExists(Integer userId);

    /**
     * Obtiene un usuario por ID SIN validar permisos
     * SOLO para comunicación INTERNA entre microservicios
     * NO usar desde endpoints públicos
     */
    UserResponse getUserByIdInternal(Integer userId);

    /**
     * Obtiene un usuario por email
     * Usado para autenticación en graphql-service
     */
    UserResponse getUserByEmail(String email);
}
