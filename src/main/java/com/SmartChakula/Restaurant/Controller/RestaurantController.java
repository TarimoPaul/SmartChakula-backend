package com.SmartChakula.Restaurant.Controller;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.SmartChakula.Restaurant.Dtos.RestaurantDto;
import com.SmartChakula.Restaurant.Services.RestaurantService;
import com.SmartChakula.Utils.GraphQlResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {
    private final RestaurantService restaurantService;

    @QueryMapping
    public GraphQlResponse<List<RestaurantDto>> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @QueryMapping
    public GraphQlResponse<RestaurantDto> getRestaurantByUid(@Argument String uid) {
        return restaurantService.getRestaurantByUid(uid);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public GraphQlResponse<RestaurantDto> saveRestaurant(@Argument RestaurantDto input, Authentication authentication) {
        return restaurantService.createRestaurant(
                authentication.getName(),
                input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public GraphQlResponse<RestaurantDto> updateRestaurant(@Argument RestaurantDto input,
            Authentication authentication) {
        return restaurantService.updateRestaurant(authentication.getName(),
                input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public GraphQlResponse<RestaurantDto> deleteRestaurant(@Argument String uid, Authentication authentication) {
        return restaurantService.deleteRestaurant(authentication.getName(), uid);
    }
}