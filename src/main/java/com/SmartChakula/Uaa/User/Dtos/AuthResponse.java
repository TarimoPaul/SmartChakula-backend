package com.SmartChakula.Uaa.User.Dtos;

import com.SmartChakula.Uaa.User.Entity.UserEntity;

public record AuthResponse(
                String token,
                UserResponseRecord user) {

        public static AuthResponse from(UserEntity user, String token) {

                return new AuthResponse(
                                token,
                                new UserResponseRecord(
                                                user.getUid(), // ✅ Changed from getId() to getUid()
                                                user.getFullName(),
                                                user.getEmail(),
                                                user.getPhone(),
                                                user.getRole().name(),
                                                user.getIsActive(),
                                                user.getCreatedAt().toString()));

        }

}