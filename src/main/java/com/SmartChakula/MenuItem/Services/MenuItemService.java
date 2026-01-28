package com.SmartChakula.MenuItem.Services;

import java.util.List;

import org.springframework.stereotype.Service;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {

    private final MenuItemRepo menuItemRepo;
    private final CategoryRepo categoryRepo;
    private final RestaurantRepo restaurantRepo;

    public List<MenuItemDto> getMenuItemsByCategory(String categoryUid) {
        log.debug("Fetching menu items for category: {}", categoryUid);
        
        ResponseList<MenuItemDto> response = getMenuItemsByCategory(categoryUid);
        
        if (response.getStatus() != ResponseStatus.Success) {
            throw new RuntimeException("Failed to fetch menu items: " + response.getMessage());
        }
        
        return response.getData();
    }

    /**
     * GraphQL-friendly method to get menu items by restaurant.
     */
    public List<MenuItemDto> getMenuItemsByRestaurant(String restaurantUid) {
        log.debug("Fetching menu items for restaurant: {}", restaurantUid);
        
        ResponseList<MenuItemDto> response = getMenuItemsByRestaurant(restaurantUid);
        
        if (response.getStatus() != ResponseStatus.Success) {
            throw new RuntimeException("Failed to fetch menu items: " + response.getMessage());
        }
        
        return response.getData();
    }

    /**
     * GraphQL-friendly method to get all menu items.
     */
    public List<MenuItemDto> getAllMenuItemsDirect() {
        log.debug("Fetching all menu items");
        
        ResponseList<MenuItemDto> response = getAllMenuItems();
        
        if (response.getStatus() != ResponseStatus.Success) {
            throw new RuntimeException("Failed to fetch menu items: " + response.getMessage());
        }
        
        return response.getData();
    }

  
    public MenuItemDto saveMenuItem(MenuItemDto input) {
        log.info("Saving menu item: {}", input.getName());
        
        // Call the existing wrapped method which contains all the business logic
        ResponseList<MenuItemDto> response = saveMenuItems(input);
        
        // Unwrap and validate the response
        if (response.getStatus() != ResponseStatus.Success) {
            log.error("Failed to save menu item: {}", response.getMessage());
            throw new RuntimeException("Failed to save menu item: " + response.getMessage());
        }
        
        List<MenuItemDto> data = response.getData();
        
        if (data == null || data.isEmpty()) {
            log.error("Service returned success but no data");
            throw new RuntimeException("Failed to save menu item - no data returned");
        }
        
        MenuItemDto savedItem = data.get(0);
        log.info("Successfully saved menu item with UID: {}", savedItem.getUid());
        
        return savedItem;
    }

    /**
     * GraphQL-friendly method to update a menu item.
     */
    public MenuItemDto updateMenuItem(String uid, MenuItemDto input) {
        log.info("Updating menu item: {}", uid);
        
        Response<MenuItemDto> response = updateMenuItem(uid, input);
        
        if (response.getStatus() != ResponseStatus.Success) {
            log.error("Failed to update menu item: {}", response.getMessage());
            throw new RuntimeException("Failed to update menu item: " + response.getMessage());
        }
        
        if (response.getData() == null) {
            log.error("Update succeeded but no data returned");
            throw new RuntimeException("Failed to update menu item - no data returned");
        }
        
        log.info("Successfully updated menu item: {}", uid);
        return response.getData();
    }

    /**
     * GraphQL-friendly method to delete a menu item.
     * Returns boolean instead of Response wrapper.
     */
    public Boolean deleteMenuItem(String uid) {
        log.info("Deleting menu item: {}", uid);
        
        Response<String> response = deleteMenuItem(uid);
        
        if (response.getStatus() != ResponseStatus.Success) {
            log.error("Failed to delete menu item: {}", response.getMessage());
            throw new RuntimeException("Failed to delete menu item: " + response.getMessage());
        }
        
        log.info("Successfully deleted menu item: {}", uid);
        return true;
    }

    private MenuItemDto mapToDto(MenuItem entity) {
        MenuItemDto dto = new MenuItemDto();
        dto.setUid(entity.getUid());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setImage(entity.getImage());
        dto.setIsAvailable(entity.isAvailable());
        dto.setCategoryUid(entity.getCategory().getUid());
        dto.setRestaurantUid(entity.getRestaurant().getUid());
        return dto;
    }
}

