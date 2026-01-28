package com.SmartChakula.Uaa.User.Dtos;


import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String uid;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private Boolean isActive;
    private String createdAt;
    
    // Helper method to create from UserEntity
    public static UserDto from(UserEntity userEntity) {
        UserDto dto = new UserDto();
        dto.setUid(userEntity.getUid());
        dto.setFullName(userEntity.getFullName());
        dto.setEmail(userEntity.getEmail());
        dto.setPhone(userEntity.getPhone());
        dto.setRole(userEntity.getRole().name());
        dto.setIsActive(userEntity.getIsActive());
        dto.setCreatedAt(userEntity.getCreatedAt() != null ? userEntity.getCreatedAt().toString() : null);
        return dto;
    }
}