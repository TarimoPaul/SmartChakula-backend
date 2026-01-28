package com.SmartChakula.Restaurant.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {
    private String status;          // "Success", "Error", etc
    private String message;
    private RestaurantDto data;     // Actual restaurant
}
