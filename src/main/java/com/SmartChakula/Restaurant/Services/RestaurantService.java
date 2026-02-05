package com.SmartChakula.Restaurant.Services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SmartChakula.Restaurant.Dtos.RestaurantDto;
import com.SmartChakula.Restaurant.Entity.RestaurantEntity;
import com.SmartChakula.Restaurant.Repository.RestaurantRepo;
import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Repository.UserRepo;
import com.SmartChakula.Utils.GraphQlResponse;
import com.SmartChakula.Utils.ResponseStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepo restaurantRepo;
    private final UserRepo userRepo;

    public GraphQlResponse<List<RestaurantDto>> getAllRestaurants() {
        try {
            log.info("Fetching all restaurants");
            List<RestaurantEntity> restaurants = restaurantRepo.findByIsActive();

            List<RestaurantDto> dtoList = restaurants.stream()
                    .map(this::mapToDto)
                    .toList();

            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Restaurants fetched successfully",
                    dtoList);
        } catch (Exception e) {
            log.error("Error fetching restaurants: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to fetch restaurants: " + e.getMessage(),
                    null);
        }
    }

    public GraphQlResponse<RestaurantDto> getRestaurantByUid(String uid) {
        try {
            log.info("Fetching restaurant with uid: {}", uid);

            if (uid == null || uid.isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "UID is required",
                        null);
            }

            RestaurantEntity restaurant = restaurantRepo.findByUid(uid).orElse(null);

            if (restaurant == null) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Restaurant not found",
                        null);
            }

            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Restaurant fetched successfully",
                    mapToDto(restaurant));
        } catch (Exception e) {
            log.error("Error fetching restaurant: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to fetch restaurant: " + e.getMessage(),
                    null);
        }
    }

    @Transactional
    public GraphQlResponse<RestaurantDto> createRestaurant(String email, RestaurantDto input) {
        try {
            log.info("Creating restaurant with name: {}", input.getName());

            // Validate required fields
            if (input.getName() == null || input.getName().isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Restaurant name is required",
                        null);
            }

            UserEntity owner = userRepo.findByEmail(email).orElse(null);
            if (owner == null) {
                return new GraphQlResponse<>(ResponseStatus.Error.toString(), "user not found", null);

            }

            if (owner.getRole() != UserRole.OWNER && owner.getRole() != UserRole.ADMIN) {
                return new GraphQlResponse<>(ResponseStatus.Warning.toString(), "You are not allowed to create restaurant", null);
            }

            // Create restaurant
            RestaurantEntity restaurant = new RestaurantEntity();
            restaurant.setUid(UUID.randomUUID().toString());
            restaurant.setName(input.getName().trim());
            restaurant.setDescription(input.getDescription());
            restaurant.setPhoneNumber(input.getPhoneNumber());
            restaurant.setRegion(input.getRegion());
            restaurant.setCity(input.getCity());
            restaurant.setIsOpen(input.getIsOpen() != null ? Boolean.parseBoolean(input.getIsOpen()) : true);
            restaurant.setOpeningTime(input.getOpeningTime());
            restaurant.setClosingTime(input.getClosingTime());
            restaurant.setOwner(owner);
            restaurant.setImage(input.getImage());
            restaurant.setType(input.getType());
            restaurant.setRank(input.getRank());
            restaurant.setAdress(input.getAdress());
            restaurant.setWebsiteUrl(input.getWebsiteUrl());
            restaurant.setDays(input.getDays());

            RestaurantEntity saved = restaurantRepo.save(restaurant);
            log.info("Restaurant created with uid: {}", saved.getUid());

            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Restaurant created successfully",
                    mapToDto(saved));
        } catch (Exception e) {
            log.error("Error creating restaurant: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to create restaurant: " + e.getMessage(),
                    null);
        }
    }

    @Transactional
    public GraphQlResponse<RestaurantDto> updateRestaurant(String email, RestaurantDto input) {
        try {
            log.info("Updating restaurant with uid: {}", input.getUid());

            if (input == null || input.getUid() == null || input.getUid().isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Restaurant UID is required",
                        null);
            }


            UserEntity user = userRepo.findByEmail(email).orElse(null);
            if (user == null) 
            return new GraphQlResponse<>(ResponseStatus.Error.toString(), "User not found", null);
                
            

            RestaurantEntity restaurant = restaurantRepo.findByUid(input.getUid()).orElse(null);
            if (restaurant == null) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Restaurant not found",
                        null);
            }

            if (!restaurant.getOwner().getUid().equals(user.getUid())
                && user.getRole() != UserRole.ADMIN){
                    return new GraphQlResponse<>(ResponseStatus.Failure.toString(), "You not allow to update this Restaurant", null);
                }
                
            

            // Update fields
            if (input.getName() != null && !input.getName().isBlank())
                restaurant.setName(input.getName());
            if (input.getDescription() != null)
                restaurant.setDescription(input.getDescription());
            if (input.getPhoneNumber() != null)
                restaurant.setPhoneNumber(input.getPhoneNumber());
            if (input.getRegion() != null)
                restaurant.setRegion(input.getRegion());
            if (input.getCity() != null)
                restaurant.setCity(input.getCity());
            if (input.getIsOpen() != null)
                restaurant.setIsOpen(Boolean.parseBoolean(input.getIsOpen()));
            if (input.getOpeningTime() != null)
                restaurant.setOpeningTime(input.getOpeningTime());
            if (input.getClosingTime() != null)
                restaurant.setClosingTime(input.getClosingTime());
            if (input.getImage() != null)
                restaurant.setImage(input.getImage());
            if (input.getType() != null)
                restaurant.setType(input.getType());
            if (input.getRank() != null)
                restaurant.setRank(input.getRank());
            if (input.getAdress() != null)
                restaurant.setAdress(input.getAdress());
            if (input.getWebsiteUrl() != null)
                restaurant.setWebsiteUrl(input.getWebsiteUrl());
            if (input.getDays() != null)
                restaurant.setDays(input.getDays());

            RestaurantEntity updated = restaurantRepo.save(restaurant);
            log.info("Restaurant updated with uid: {}", updated.getUid());

            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Restaurant updated successfully",
                    mapToDto(updated));
        } catch (Exception e) {
            log.error("Error updating restaurant: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to update restaurant: " + e.getMessage(),
                    null);
        }
    }

    @Transactional
    public GraphQlResponse<RestaurantDto> deleteRestaurant(String email, String uid) {
        try {
            log.info("Deleting restaurant with uid: {}", uid);

            if (uid == null || uid.isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "UID is required",
                        null);
            }

            UserEntity user = userRepo.findByEmail(email).orElse(null);
                if (user == null) {
                    
            
                return new GraphQlResponse<>(ResponseStatus.Error.toString(), "User not found", null);

            }

            RestaurantEntity restaurant = restaurantRepo.findByUid(uid).orElse(null);
            if (restaurant == null) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Restaurant not found",
                        null);
            }

            if (!restaurant.getOwner().getUid().equals(user.getUid())
                && user.getRole() != UserRole.ADMIN){
                    return new GraphQlResponse<>(ResponseStatus.Failure.toString(), "Youre not allowed to delete this restaurant", null);
                } 
            
                
            

            // Soft delete
            restaurant.delete();
            restaurantRepo.save(restaurant);
            log.info("Restaurant deleted with uid: {}", uid);

            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Restaurant deleted successfully",
                    null);
        } catch (Exception e) {
            log.error("Error deleting restaurant: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to delete restaurant: " + e.getMessage(),
                    null);
        }
    }

    private RestaurantDto mapToDto(RestaurantEntity entity) {
        if (entity == null) {
            return null;
        }

        RestaurantDto dto = new RestaurantDto();
        dto.setUid(entity.getUid());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setRegion(entity.getRegion());
        dto.setCity(entity.getCity());
        dto.setIsOpen(entity.getIsOpen() != null ? entity.getIsOpen().toString() : "true");
        dto.setOpeningTime(entity.getOpeningTime());
        dto.setClosingTime(entity.getClosingTime());
        dto.setOwnerUid(entity.getOwner().getUid());
        dto.setImage(entity.getImage());
        dto.setType(entity.getType());
        dto.setRank(entity.getRank());
        dto.setAdress(entity.getAdress());
        dto.setWebsiteUrl(entity.getWebsiteUrl());
        dto.setDays(entity.getDays());

        return dto;
    }
}