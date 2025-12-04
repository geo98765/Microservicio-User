package com.example.user_service.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer userId;
    private String email;
    private String userType;

    // Campos adicionales para autenticación en graphql-service
    // Solo se usan en endpoints internos
    private String password; // Password hasheado (BCrypt)
    private Set<String> roles; // Roles del usuario (ROLE_USER, ROLE_ADMIN)

    private ProfileResponse profile;
    private AccountStatusResponse accountStatus;
    private String createdAt;
    private String updatedAt;
}
