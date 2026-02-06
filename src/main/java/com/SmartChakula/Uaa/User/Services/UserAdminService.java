package com.SmartChakula.Uaa.User.Services;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SmartChakula.Uaa.User.Dtos.UserDto;
import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream().map(UserDto::from).toList();
    }

    public UserDto updateUserRole(String uid, UserRole role) {
        UserEntity user = userRepo.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(role);
        UserEntity saved = userRepo.save(user);
        return UserDto.from(saved);
    }

    public UserDto changeUserPassword(String uid, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        UserEntity user = userRepo.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        UserEntity saved = userRepo.save(user);
        return UserDto.from(saved);
    }
}
