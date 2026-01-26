package com.SmartChakula.Restaurant.Controller;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.SmartChakula.Restaurant.Dtos.RestaurantDto;
import com.SmartChakula.Restaurant.Services.RestaurantService;
import com.SmartChakula.Utils.Response;
import com.SmartChakula.Utils.ResponseList;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @QueryMapping
    public ResponseList<RestaurantDto> restaurants() {
        return restaurantService.getAllRestaurants();
    }
    
    @QueryMapping
    public Response<RestaurantDto> restaurantByUid(@Argument String uid) {
        return restaurantService.getRestaurantByUid(uid);
    }

    // ======================
    // MUTATIONS
    // ======================

    @MutationMapping
    public Response<RestaurantDto> createRestaurant(@Argument RestaurantDto input) {
        return restaurantService.createRestaurant(input);
    }

    @MutationMapping
    public Response<RestaurantDto> updateRestaurant(@Argument RestaurantDto input) {
        return restaurantService.updateRestaurant(input);
    }

    @MutationMapping
    public Response<String> deleteRestaurant(@Argument String uid) {
        return restaurantService.deleteRestaurant(uid);
    }
}