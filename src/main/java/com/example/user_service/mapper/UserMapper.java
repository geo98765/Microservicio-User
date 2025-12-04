package com.example.user_service.mapper;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.example.user_service.dto.*;
import com.example.user_service.model.Profile;
import com.example.user_service.model.ProfileLocation;
import com.example.user_service.model.User;

@Component
public class UserMapper {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

        public User toEntity(UserRequest request) {
                return User.builder()
                                .email(request.getEmail())
                                .password(request.getPassword())
                                .userType("USER")
                                .build();
        }

        public UserResponse toResponse(User user) {
                Profile profile = user.getProfiles() != null && !user.getProfiles().isEmpty()
                                ? user.getProfiles().get(0)
                                : null;

                return UserResponse.builder()
                                .userId(user.getUserId())
                                .email(user.getEmail())
                                .userType(user.getUserType())
                                // CRITICO: Incluir password y roles para autenticación de graphql-service
                                .password(user.getPassword()) // Password hasheado (BCrypt)
                                .roles(user.getRoles()) // Roles del usuario
                                .profile(profile != null ? toProfileResponse(profile) : null)
                                .accountStatus(toAccountStatusResponse(user))
                                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().format(FORMATTER) : null)
                                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().format(FORMATTER) : null)
                                .build();
        }

        public ProfileResponse toProfileResponse(Profile profile) {
                return ProfileResponse.builder()
                                .profileId(profile.getProfileId())
                                .name(profile.getName())
                                .location(profile.getProfileLocation() != null
                                                ? toProfileLocationResponse(profile.getProfileLocation())
                                                : null)
                                .build();
        }

        public ProfileLocationResponse toProfileLocationResponse(ProfileLocation location) {
                return ProfileLocationResponse.builder()
                                .profileLocationId(location.getProfileLocationId())
                                .municipality(location.getMunicipality())
                                .state(location.getState())
                                .country(location.getCountry())
                                .build();
        }

        public AccountStatusResponse toAccountStatusResponse(User user) {
                return AccountStatusResponse.builder()
                                .enabled(user.isEnabled())
                                .accountNonExpired(user.isAccountNonExpired())
                                .accountNonLocked(user.isAccountNonLocked())
                                .credentialsNonExpired(user.isCredentialsNonExpired())
                                .build();
        }
}
