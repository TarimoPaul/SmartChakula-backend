package com.SmartChakula.MenuItem.Services;

import com.SmartChakula.Category.Entity.CategoryEntity;
import com.SmartChakula.Category.Repository.CategoryRepo;
import com.SmartChakula.MenuItem.Dtos.MenuItemDto;
import com.SmartChakula.MenuItem.Entity.MenuItem;
import com.SmartChakula.MenuItem.Repository.MenuItemRepo;
import com.SmartChakula.Restaurant.Entity.RestaurantEntity;
import com.SmartChakula.Restaurant.Repository.RestaurantRepo;
import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Repository.UserRepo;
import com.SmartChakula.Utils.GraphQlListResponse;
import com.SmartChakula.Utils.GraphQlResponse;
import com.SmartChakula.Utils.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {

    private final MenuItemRepo menuItemRepo;
    private final CategoryRepo categoryRepo;
    private final RestaurantRepo restaurantRepo;
    private final UserRepo userRepo;

    public GraphQlListResponse<MenuItemDto> getMenuItemsByCategory(String categoryUid) {
        try {
            log.info("Fetching menu items by category: {}", categoryUid);
            List<MenuItem> items = menuItemRepo.findByCategoryUid(categoryUid);
            List<MenuItemDto> dtoList = items.stream().map(this::mapToDto).toList();
            
            return new GraphQlListResponse<>(
                ResponseStatus.Success.toString(),
                "Menu items fetched successfully",
                dtoList
            );
        } catch (Exception e) {
            log.error("Error fetching menu items by category: {}", e.getMessage(), e);
            return new GraphQlListResponse<>(
                ResponseStatus.Error.toString(),
                "Failed to fetch menu items: " + e.getMessage(),
                null
            );
        }
    }

    public GraphQlListResponse<MenuItemDto> getMenuItemsByRestaurant(String restaurantUid) {
        try {
            log.info("Fetching menu items by restaurant: {}", restaurantUid);
            List<MenuItem> items = menuItemRepo.findByRestaurantUid(restaurantUid);
            List<MenuItemDto> dtoList = items.stream().map(this::mapToDto).toList();
            
            return new GraphQlListResponse<>(
                ResponseStatus.Success.toString(),
                "Menu items fetched successfully",
                dtoList
            );
        } catch (Exception e) {
            log.error("Error fetching menu items by restaurant: {}", e.getMessage(), e);
            return new GraphQlListResponse<>(
                ResponseStatus.Error.toString(),
                "Failed to fetch menu items: " + e.getMessage(),
                null
            );
        }
    }

    public GraphQlListResponse<MenuItemDto> getAllMenuItems() {
        try {
            log.info("Fetching all menu items");
            List<MenuItem> items = menuItemRepo.findAllActive();
            List<MenuItemDto> dtoList = items.stream().map(this::mapToDto).toList();
            
            return new GraphQlListResponse<>(
                ResponseStatus.Success.toString(),
                "Menu items fetched successfully",
                dtoList
            );
        } catch (Exception e) {
            log.error("Error fetching all menu items: {}", e.getMessage(), e);
            return new GraphQlListResponse<>(
                ResponseStatus.Error.toString(),
                "Failed to fetch menu items: " + e.getMessage(),
                null
            );
        }
    }

    public GraphQlResponse<MenuItemDto> getMenuItem(String uid) {
        try {
            log.info("Fetching menu item with uid: {}", uid);
            
            if (uid == null || uid.isBlank()) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "UID is required",
                    null
                );
            }

            MenuItem menuItem = menuItemRepo.findByUid(uid).orElse(null);

            if (menuItem == null) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Menu item not found",
                    null
                );
            }

            return new GraphQlResponse<>(
                ResponseStatus.Success.toString(),
                "Menu item fetched successfully",
                mapToDto(menuItem)
            );
        } catch (Exception e) {
            log.error("Error fetching menu item: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                ResponseStatus.Error.toString(),
                "Failed to fetch menu item: " + e.getMessage(),
                null
            );
        }
    }

    @Transactional
    public GraphQlResponse<MenuItemDto> saveMenuItem(String email, MenuItemDto input) {

        try {

            if (input == null) {
                log.error("Input is null");
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Input is required",
                    null
                );
            }

            UserEntity user = userRepo.findByEmail(email).orElse(null);
            if (user == null)
                return new GraphQlResponse<>(ResponseStatus.Error.toString(), "user not found", null);        
            

            CategoryEntity category = categoryRepo.findByUid(input.getCategoryUid()).orElse(null);
            if (category == null)
                return new GraphQlResponse<>(ResponseStatus.Error.toString(), "category not found", null);


            RestaurantEntity restaurant = category.getRestaurant();

            if (!restaurant.getOwner().getUid().equals(user.getUid())
                && user.getRole() != UserRole.ADMIN) {

                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Unauthorized: You do not own the restaurant for this category",
                    null
                );
            }

            // Validate required fields
            if (input.getName() == null || input.getName().isBlank()) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Name is required",
                    null
                );
            }

            if (input.getCategoryUid() == null || input.getCategoryUid().isBlank()) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Category UID is required",
                    null
                );
            }

            if (input.getRestaurantUid() == null || input.getRestaurantUid().isBlank()) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Restaurant UID is required",
                    null
                );
            }

            if (input.getPrice() == null || input.getPrice() <= 0) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Valid price is required",
                    null
                );
            }

            // Generate UID
            String uid = UUID.randomUUID().toString();
            log.info("Generated UID: {}", uid);

            // Prepare data with validation
            String name = input.getName().trim();
            String description = input.getDescription() != null ? input.getDescription().trim() : null;
            String image = input.getImage();
            boolean available = input.getAvailable() != null ? input.getAvailable() : true;

            String warning = "";

            // Validate and truncate description if needed
            if (description != null && description.length() > 255) {
                description = description.substring(0, 255);
                warning = "Description truncated to 255 characters. ";
            }

            // Validate image URL length
            if (image != null && image.length() > 2555) {
                image = null;
                warning += "Image URL too long, removed. ";
            }

            // Insert menu item
            log.info("Inserting menu item with UID: {}", uid);
            int rowsInserted = menuItemRepo.insertMenuItem(
                    uid,
                    name,
                    description,
                    input.getPrice(),
                    image,
                    available,
                    category.getId(),
                    restaurant.getId());

            if (rowsInserted == 0) {
                log.error("Failed to insert menu item into database");
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to save menu item to database",
                    null
                );
            }

            // Fetch the saved menu item
            log.info("Fetching saved menu item with UID: {}", uid);
            MenuItem savedMenuItem = menuItemRepo.findByUid(uid)
                    .orElseThrow(() -> new RuntimeException("Menu item not found after insertion"));

            // Map to DTO
            MenuItemDto resultDto = mapToDto(savedMenuItem);

            // Prepare response message
            String message = warning.isEmpty()
                    ? "Menu item saved successfully"
                    : "Menu item saved successfully. " + warning.trim();

            return new GraphQlResponse<>(
                ResponseStatus.Success.toString(),
                message,
                resultDto
            );

        } catch (Exception e) {
            log.error("Error in saveMenuItem: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                ResponseStatus.Error.toString(),
                "Failed to save menu item: " + e.getMessage(),
                null
            );
        }
    }

    @Transactional
    public GraphQlResponse<MenuItemDto> updateMenuItem(String email, String uid, MenuItemDto input) {
        try {
            log.info("Updating menu item with uid: {}", uid);
            
            if (uid == null || uid.isBlank()) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "UID is required",
                    null
                );
            }

            if (input == null) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Input is required",
                    null
                );
            }

            // Validate required fields
            if (input.getName() == null || input.getName().isBlank()) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Name is required",
                    null
                );
            }

            if (input.getPrice() == null || input.getPrice() <= 0) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Valid price is required",
                    null
                );
            }


            UserEntity user = userRepo.findByEmail(email).orElse(null);
            if (user == null)
                return new GraphQlResponse<>(ResponseStatus.Error.toString(), "user not found", null);


            // Check if menu item exists
            MenuItem existingItem = menuItemRepo.findByUid(uid).orElse(null);
            if (existingItem == null) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Menu item not found",
                    null
                );
            }

            RestaurantEntity restaurant = existingItem.getCategory().getRestaurant();

            if (!restaurant.getOwner().getUid().equals(user.getUid())
                && user.getRole() != UserRole.ADMIN) {

                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Unauthorized: You do not own the restaurant for this menu item",
                    null
                );
                
            }
            // Prepare data
            String name = input.getName().trim();
            String description = input.getDescription() != null ? input.getDescription().trim() : null;
            String image = input.getImage();
            boolean available = input.getAvailable() != null ? input.getAvailable() : existingItem.isAvailable();

            String warning = "";

            // Validate and truncate description if needed
            if (description != null && description.length() > 255) {
                description = description.substring(0, 255);
                warning = "Description truncated to 255 characters. ";
            }

            // Validate image URL length
            if (image != null && image.length() > 2555) {
                image = null;
                warning += "Image URL too long, removed. ";
            }

            // Update menu item
            int rowsUpdated = menuItemRepo.updateMenuItem(
                    uid,
                    name,
                    description,
                    input.getPrice(),
                    image,
                    available);

            if (rowsUpdated == 0) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to update menu item",
                    null
                );
            }

            // Fetch updated menu item
            MenuItem updatedMenuItem = menuItemRepo.findByUid(uid)
                    .orElseThrow(() -> new RuntimeException("Menu item not found after update"));

            // Prepare response message
            String message = warning.isEmpty()
                    ? "Menu item updated successfully"
                    : "Menu item updated successfully. " + warning.trim();

            return new GraphQlResponse<>(
                ResponseStatus.Success.toString(),
                message,
                mapToDto(updatedMenuItem)
            );

        } catch (Exception e) {
            log.error("Error in updateMenuItem: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                ResponseStatus.Error.toString(),
                "Failed to update menu item: " + e.getMessage(),
                null
            );
        }
    }

    @Transactional
    public GraphQlResponse<MenuItemDto> deleteMenuItem(String email, String uid) {
        try {
            log.info("Deleting menu item with uid: {}", uid);
            
            if (uid == null || uid.isBlank()) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "UID is required",
                    null
                );
            }

            UserEntity user = userRepo.findByEmail(email).orElse(null);
            if (user == null)
                return new GraphQlResponse<>(ResponseStatus.Error.toString(), "user not found", null);

            // Check if menu item exists
            MenuItem existingItem = menuItemRepo.findByUid(uid).orElse(null);
            if (existingItem == null) {
                return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Menu item not found",
                    null
                );
            }

            if (!existingItem.getRestaurant().getOwner().getUid().equals(user.getUid())
                && user.getRole() != UserRole.ADMIN) {
                return new GraphQlResponse<>(ResponseStatus.Error.toString(),
                    "Unauthorized: You do not own the restaurant for this menu item",
                    null
                );
            }

           menuItemRepo.softDeleteMenuItem(uid);
           return new GraphQlResponse<>(
                ResponseStatus.Success.toString(),
                "Menu item deleted successfully",
                null
            );


        } catch (Exception e) {
            log.error("Error in deleteMenuItem: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                ResponseStatus.Error.toString(),
                "Failed to delete menu item: " + e.getMessage(),
                null
            );
        }
    }

    private MenuItemDto mapToDto(MenuItem entity) {
        if (entity == null) {
            return null;
        }

        MenuItemDto dto = new MenuItemDto();
        dto.setUid(entity.getUid());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setImage(entity.getImage());
        dto.setAvailable(entity.isAvailable());
        dto.setCategoryUid(entity.getCategory().getUid());
        dto.setRestaurantUid(entity.getRestaurant().getUid());

        return dto;
    }
}