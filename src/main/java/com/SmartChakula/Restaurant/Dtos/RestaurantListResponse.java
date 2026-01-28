package com.SmartChakula.Restaurant.Dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantListResponse {
    private String status;
    private String message;
    private List<RestaurantDto> data;
}