package com.SmartChakula.MenuItem.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDto {

    private String uid;
    private String name;
    private String description;
    private Double price;
    private String image;
    private Boolean isAvailable;
    private String categoryUid;
    private String restaurantUid;
    private String isActive;

}
