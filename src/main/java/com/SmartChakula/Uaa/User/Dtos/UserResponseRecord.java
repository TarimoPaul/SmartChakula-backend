package com.SmartChakula.Uaa.User.Dtos;

public record UserResponseRecord(
        String uid,
        String fullName,
        String email,
        String phone,
        String role,
        Boolean isActive,
        String createdAt) {
}