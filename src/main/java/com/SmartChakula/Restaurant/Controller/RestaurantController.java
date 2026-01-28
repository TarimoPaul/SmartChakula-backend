package com.SmartChakula.Restaurant.Controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.SmartChakula.Restaurant.Dtos.RestaurantDto;
import com.SmartChakula.Restaurant.Dtos.RestaurantResponse;
import com.SmartChakula.Restaurant.Dtos.RestaurantListResponse;
import com.SmartChakula.Restaurant.Services.RestaurantService;
import com.SmartChakula.Utils.Response;
import com.SmartChakula.Utils.ResponseList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {
    private final RestaurantService restaurantService;

    @QueryMapping
    public RestaurantListResponse getAllRestaurants() {
        log.info("getAllRestaurants query called");
        ResponseList<RestaurantDto> serviceResponse = restaurantService.getAllRestaurants();

        // Map ResponseList to RestaurantListResponse
        return new RestaurantListResponse(
                serviceResponse.getStatus().toString(),
                serviceResponse.getMessage(),
                serviceResponse.getData());
    }

    @QueryMapping
    public RestaurantResponse restaurantByUid(@Argument String uid) {
        log.info("restaurantByUid query called with uid: {}", uid);
        Response<RestaurantDto> serviceResponse = restaurantService.getRestaurantByUid(uid);

        // Map Response to RestaurantResponse
        return new RestaurantResponse(
                serviceResponse.getStatus().toString(),
                serviceResponse.getMessage(),
                serviceResponse.getData());
    }

    @MutationMapping
    public RestaurantResponse createRestaurant(@Argument RestaurantDto input) {
        log.info("createRestaurant mutation called with name: {}", input.getName());
        Response<RestaurantDto> serviceResponse = restaurantService.createRestaurant(input);

        return new RestaurantResponse(
                serviceResponse.getStatus().toString(),
                serviceResponse.getMessage(),
                serviceResponse.getData());
    }

    @MutationMapping
    public RestaurantResponse updateRestaurant(@Argument RestaurantDto input) {
        log.info("updateRestaurant mutation called");
        Response<RestaurantDto> serviceResponse = restaurantService.updateRestaurant(input);

        return new RestaurantResponse(
                serviceResponse.getStatus().toString(),
                serviceResponse.getMessage(),
                serviceResponse.getData());
    }

    @MutationMapping
    public RestaurantResponse deleteRestaurant(@Argument String uid) {
        log.info("deleteRestaurant mutation called with uid: {}", uid);
        Response<String> serviceResponse = restaurantService.deleteRestaurant(uid);

        // deleteRestaurant inarudisha String, si data, kwa hiyo data ni null
        return new RestaurantResponse(
                serviceResponse.getStatus().toString(),
                serviceResponse.getMessage(),
                null);
    }
}