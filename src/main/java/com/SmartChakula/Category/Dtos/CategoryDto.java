package com.SmartChakula.Category.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private String name;
    private String description;
    private String restaurantUid;
    private String isActive;
    private String uid;

}
