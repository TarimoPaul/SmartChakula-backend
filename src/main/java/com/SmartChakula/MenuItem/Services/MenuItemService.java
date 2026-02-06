package com.SmartChakula.MenuItem.Services;

import com.SmartChakula.Category.Entity.CategoryEntity;
import com.SmartChakula.Category.Repository.CategoryRepo;
import com.SmartChakula.MenuItem.Dtos.MenuItemDto;
import com.SmartChakula.MenuItem.Entity.MenuItem;
import com.SmartChakula.MenuItem.Repository.MenuItemRepo;
import com.SmartChakula.Restaurant.Entity.RestaurantEntity;
import com.SmartChakula.Restaurant.Repository.RestaurantRepo;
import com.SmartChakula.Utils.Response;
import com.SmartChakula.Utils.ResponseList;
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

    public ResponseList<MenuItemDto> getMenuItemsByCategory(String categoryUid) {
        try {
            List<MenuItem> items = menuItemRepo.findByCategoryUid(categoryUid);
            List<MenuItemDto> dtoList = items.stream().map(this::mapToDto).toList();
            return ResponseList.success(dtoList, "Menu items fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching menu items by category: {}", e.getMessage(), e);
            return ResponseList.error("Failed to fetch menu items: " + e.getMessage());
        }
    }

    public ResponseList<MenuItemDto> getMenuItemsByRestaurant(String restaurantUid) {
        try {
            List<MenuItem> items = menuItemRepo.findByRestaurantUid(restaurantUid);
            List<MenuItemDto> dtoList = items.stream().map(this::mapToDto).toList();
            return ResponseList.success(dtoList, "Menu items fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching menu items by restaurant: {}", e.getMessage(), e);
            return ResponseList.error("Failed to fetch menu items: " + e.getMessage());
        }
    }

    public ResponseList<MenuItemDto> getAllMenuItems() {
        try {
            List<MenuItem> items = menuItemRepo.findAllActive();
            List<MenuItemDto> dtoList = items.stream().map(this::mapToDto).toList();
            return ResponseList.success(dtoList, "Menu items fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching all menu items: {}", e.getMessage(), e);
            return ResponseList.error("Failed to fetch menu items: " + e.getMessage());
        }
    }

    public Response<MenuItemDto> getMenuItem(String uid) {
        try {
            if (uid == null || uid.isBlank()) {
                return new Response<>(ResponseStatus.Error, null, "UID is required");
            }

            MenuItem menuItem = menuItemRepo.findByUid(uid)
                    .orElse(null);

            if (menuItem == null) {
                return new Response<>(ResponseStatus.Error, null, "Menu item not found");
            }

            return new Response<>(ResponseStatus.Success, mapToDto(menuItem), "Menu item fetched successfully");
        } catch (Exception e) {
            log.error("Error fetching menu item: {}", e.getMessage(), e);
            return new Response<>(ResponseStatus.Error, null, "Failed to fetch menu item: " + e.getMessage());
        }
    }

    @Transactional
    public Response<MenuItemDto> saveMenuItem(MenuItemDto input) {
        log.info("saveMenuItem called with input: {}", input);

        try {
            if (input == null) {
                log.error("Input is null");
                return new Response<>(ResponseStatus.Error, null, "Input is required");
            }

            // Validate required fields
            if (input.getName() == null || input.getName().isBlank()) {
                return new Response<>(ResponseStatus.Error, null, "Name is required");
            }

            if (input.getCategoryUid() == null || input.getCategoryUid().isBlank()) {
                return new Response<>(ResponseStatus.Error, null, "Category UID is required");
            }

            if (input.getRestaurantUid() == null || input.getRestaurantUid().isBlank()) {
                return new Response<>(ResponseStatus.Error, null, "Restaurant UID is required");
            }

            if (input.getPrice() == null || input.getPrice() <= 0) {
                return new Response<>(ResponseStatus.Error, null, "Valid price is required");
            }

            // Fetch category
            CategoryEntity category = categoryRepo.findByUid(input.getCategoryUid())
                    .orElse(null);
            if (category == null) {
                return new Response<>(ResponseStatus.Error, null, "Category not found with UID: " + input.getCategoryUid());
            }

            // Fetch restaurant
            RestaurantEntity restaurant = restaurantRepo.findByUid(input.getRestaurantUid())
                    .orElse(null);
            if (restaurant == null) {
                return new Response<>(ResponseStatus.Error, null, "Restaurant not found with UID: " + input.getRestaurantUid());
            }

            // Generate UID
            String uid = UUID.randomUUID().toString();
            log.info("Generated UID: {}", uid);

            // Prepare data with validation
            String name = input.getName().trim();
            String description = input.getDescription() != null ? input.getDescription().trim() : null;
            String image = input.getImage();
            boolean available = input.getAvailable() != null ? input.getAvailable() : true;

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
                return new Response<>(ResponseStatus.Error, null, "Failed to save menu item to database");
            }

            // Fetch the saved menu item
            log.info("Fetching saved menu item with UID: {}", uid);
            MenuItem savedMenuItem = menuItemRepo.findByUid(uid)
                    .orElseThrow(() -> new RuntimeException("Menu item not found after insertion"));

            // Map to DTO
            MenuItemDto resultDto = mapToDto(savedMenuItem);

            return new Response<>(ResponseStatus.Success, resultDto, "Menu item saved successfully");

        } catch (Exception e) {
            log.error("Error in saveMenuItem: {}", e.getMessage(), e);
            return new Response<>(ResponseStatus.Error, null, "Failed to save menu item: " + e.getMessage());
        }
    }

    @Transactional
    public Response<MenuItemDto> updateMenuItem(String uid, MenuItemDto input) {
        try {
            if (uid == null || uid.isBlank()) {
                return new Response<>(ResponseStatus.Error, null, "UID is required");
            }

            if (input == null) {
                return new Response<>(ResponseStatus.Error, null, "Input is required");
            }

            // Validate required fields
            if (input.getName() == null || input.getName().isBlank()) {
                return new Response<>(ResponseStatus.Error, null, "Name is required");
            }

            if (input.getPrice() == null || input.getPrice() <= 0) {
                return new Response<>(ResponseStatus.Error, null, "Valid price is required");
            }

            // Check if menu item exists
            MenuItem existingItem = menuItemRepo.findByUid(uid)
                    .orElse(null);
            if (existingItem == null) {
                return new Response<>(ResponseStatus.Error, null, "Menu item not found");
            }

            // Prepare data
            String name = input.getName().trim();
            String description = input.getDescription() != null ? input.getDescription().trim() : null;
            String image = input.getImage();
            boolean available = input.getAvailable() != null ? input.getAvailable() : existingItem.isAvailable();

            // Update menu item
            int rowsUpdated = menuItemRepo.updateMenuItem(
                    uid,
                    name,
                    description,
                    input.getPrice(),
                    image,
                    available);

            if (rowsUpdated == 0) {
                return new Response<>(ResponseStatus.Failure, null, "Failed to update menu item");
            }

            // Fetch updated menu item
            MenuItem updatedMenuItem = menuItemRepo.findByUid(uid)
                    .orElseThrow(() -> new RuntimeException("Menu item not found after update"));

            return new Response<>(ResponseStatus.Success, mapToDto(updatedMenuItem), "Menu item updated successfully");

        } catch (Exception e) {
            log.error("Error in updateMenuItem: {}", e.getMessage(), e);
            return new Response<>(ResponseStatus.Error, null, "Failed to update menu item: " + e.getMessage());
        }
    }

    @Transactional
    public Response<String> deleteMenuItem(String uid) {
        try {
            if (uid == null || uid.isBlank()) {
                return new Response<>("UID is required");
            }

            // Check if menu item exists
            MenuItem existingItem = menuItemRepo.findByUid(uid)
                    .orElse(null);
            if (existingItem == null) {
                return new Response<>("Menu item not found");
            }

            // Soft delete
            int rowsDeleted = menuItemRepo.softDeleteMenuItem(uid);

            if (rowsDeleted == 0) {
                return new Response<>("Failed to delete menu item");
            }

            return new Response<>( "Menu item deleted successfully");

        } catch (Exception e) {
            log.error("Error in deleteMenuItem: {}", e.getMessage(), e);
            return new Response<>("Failed to delete menu item: " + e.getMessage());
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