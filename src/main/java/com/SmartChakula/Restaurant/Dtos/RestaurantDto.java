package com.SmartChakula.Restaurant.Dtos;

import com.SmartChakula.Uaa.User.Dtos.UserResponseRecord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {

    private String uid;
    private String name;
    private String description;
    private String phoneNumber;
    private String region;
    private String city;
    private String isOpen;
    private String openingTime;
    private String closingTime;
    private String ownerUid;
    private String image;
     private String type;
    private String rank;
    private String adress;
    private String websiteUrl;
    private String days;

    private UserResponseRecord owner;


}
