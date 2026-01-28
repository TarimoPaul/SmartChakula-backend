package com.SmartChakula.Uaa.User.Dtos;

import com.SmartChakula.Uaa.User.Entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String status;
    private String message;
    private String token;
    private UserDto user;
    
    // Helper method to create from UserEntity
    public static AuthResponse from(UserEntity userEntity, String token) {
        AuthResponse response = new AuthResponse();
        response.setStatus("Success");
        response.setMessage("Authentication successful");
        response.setToken(token);
        response.setUser(UserDto.from(userEntity));
        return response;
    }
}