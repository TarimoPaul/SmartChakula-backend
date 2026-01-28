package com.SmartChakula.Category.Dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryInput  {
    private String uid;
    private String name;
    private String description;
    private String image;
    private String restaurantUid;
    
    public String getUid() {
        return uid;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getImage() {
        return image;
    }
    
    public String getRestaurantUid() {
        return restaurantUid;
    }
}