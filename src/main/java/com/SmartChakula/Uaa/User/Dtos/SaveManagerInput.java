package com.SmartChakula.Uaa.User.Dtos;

import java.util.List;

import lombok.Data;

@Data
public class SaveManagerInput {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private List<String> restaurantUids;
}