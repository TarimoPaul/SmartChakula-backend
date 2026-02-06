package com.SmartChakula.Uaa.User.Dtos;

import lombok.Data;

@Data
public class SaveOwnerInput {
    private String fullName;
    private String email;
    private String password;
    private String phone;
}
