package com.SmartChakula.Restaurant.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.SmartChakula.Restaurant.Dtos.RestaurantDto;
import com.SmartChakula.Restaurant.Entity.RestaurantEntity;
import com.SmartChakula.Restaurant.Repository.RestaurantRepo;
import com.SmartChakula.Uaa.User.Dtos.UserResponseRecord;
import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Repository.UserRepo;
import com.SmartChakula.Utils.Response;
import com.SmartChakula.Utils.ResponseList;
import com.SmartChakula.Utils.ResponseStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepo restaurantRepo;
    private final UserRepo userRepo;

    public ResponseList<RestaurantDto> getAllRestaurants() {
        List<RestaurantEntity> restaurants = restaurantRepo.findByIsActive();

        List<RestaurantDto> dtoList = restaurants.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseList.success(dtoList, "Restaurants fetched successfully");
    }

    // Get one restaurant by UID
    public Response<RestaurantDto> getRestaurantByUid(String uid) {
        RestaurantEntity restaurant = restaurantRepo.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return new Response<>(ResponseStatus.Success, mapToResponse(restaurant), "Restaurant fetched successfully");
    }

    public Response<RestaurantDto> createRestaurant(RestaurantDto input) {

        String ownerUid = input.getOwnerUid();
        
        if (ownerUid == null || ownerUid.isEmpty()) {
            throw new RuntimeException("Owner UID is required");
        }
        
        UserEntity owner = userRepo.findByUid(ownerUid)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName(input.getName());
        restaurant.setDescription(input.getDescription());
        restaurant.setPhoneNumber(input.getPhoneNumber());
        restaurant.setRegion(input.getRegion());
        restaurant.setCity(input.getCity());
        restaurant.setIsOpen(input.getIsOpen() != null ? Boolean.parseBoolean(input.getIsOpen()) : true);
        restaurant.setOpeningTime(input.getOpeningTime());
        restaurant.setClosingTime(input.getClosingTime());
        restaurant.setOwner(owner);

        RestaurantEntity saved = restaurantRepo.save(restaurant);

        return new Response<>(ResponseStatus.Success, mapToResponse(saved), "Restaurant created successfully");
    }

    // Soft delete
    public Response<String> deleteRestaurant(String uid) {
        RestaurantEntity r = restaurantRepo.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        r.delete();
        restaurantRepo.save(r);

        return new Response<>(ResponseStatus.Success, "Restaurant deleted successfully", "Restaurant deleted successfully");
    }

    private RestaurantDto mapToResponse(RestaurantEntity entity) {
        UserEntity owner = entity.getOwner();
        
        return new RestaurantDto(
                entity.getUid(),
                entity.getName(),
                entity.getDescription(),
                entity.getPhoneNumber(),
                entity.getRegion(),
                entity.getCity(),
                entity.getIsOpen() != null ? entity.getIsOpen().toString() : "true",
                entity.getOpeningTime(),
                entity.getClosingTime(),
                owner.getUid(),
                new UserResponseRecord(
                        owner.getUid(),
                        owner.getFullName(),
                        owner.getEmail(),
                        owner.getPhone(),
                        owner.getRole().name(),
                        owner.getIsActive(),
                        owner.getCreatedAt() != null ? owner.getCreatedAt().toString() : null));
    }
}