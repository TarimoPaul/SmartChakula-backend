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

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepo menuItemRepo;
    private final CategoryRepo categoryRepo;
    private final RestaurantRepo restaurantRepo;

    public ResponseList<MenuItemDto> getMenuItemsByCategory(String categoryUid) {

        List<MenuItem> items = menuItemRepo.findByCategoryUid(categoryUid);

        List<MenuItemDto> dtoList = items.stream()
                .map(this::mapToDto)
                .toList();

        return ResponseList.success(dtoList, "Menu items fetched successfully");
    }

    public ResponseList<MenuItemDto> getMenuItemsByRestaurant(String restaurantUid) {

        List<MenuItem> menuItems = menuItemRepo.findByRestaurantUid(restaurantUid);

        List<MenuItemDto> menuItemDtos = menuItems.stream()
                .map(this::mapToDto)
                .toList();

        return ResponseList.success(menuItemDtos, "Menu items fetched successfully");
    }

    public Response<MenuItemDto> getMenuItem(String uid) {

        MenuItem menuItem = menuItemRepo.findByUid(uid)
                .orElse(null);

        if (menuItem == null) {
            return new Response<>("Menu item not found", ResponseStatus.Error);
        }

        return new Response<>(ResponseStatus.Success, mapToDto(menuItem), "Menu item fetched successfully");
    }

    public ResponseList<MenuItemDto> getAllMenuItems() {

        List<MenuItem> menuItems = menuItemRepo.findAllActive();

        List<MenuItemDto> menuItemDtos = menuItems.stream()
                .map(this::mapToDto)
                .toList();

        return ResponseList.success(menuItemDtos, "Menu items fetched successfully");
    }

    public ResponseList<MenuItemDto> saveMenuItems(MenuItemDto input) {

        try {
            if (input == null) {
                return ResponseList.error("Input is required");
            }
            if (input.getCategoryUid() == null || input.getCategoryUid().isBlank()) {
                return ResponseList.error("categoryUid is required");
            }
            if (input.getRestaurantUid() == null || input.getRestaurantUid().isBlank()) {
                return ResponseList.error("restaurantUid is required");
            }

            CategoryEntity category = categoryRepo.findByUid(input.getCategoryUid())
                    .orElse(null);
            if (category == null) {
                return ResponseList.error("Category not found");
            }

            RestaurantEntity restaurant = restaurantRepo.findByUid(input.getRestaurantUid())
                    .orElse(null);
            if (restaurant == null) {
                return ResponseList.error("Restaurant not found");
            }

            String uid = java.util.UUID.randomUUID().toString();

            String description = input.getDescription();
            String image = input.getImage();
            String warning = "";
            if (description != null && description.length() > 255) {
                description = description.substring(0, 255);
                warning = "Description was truncated to 255 characters.";
            }
            if (image != null && image.length() > 255) {
                image = null;
                warning = warning.isBlank() ? "Image was not saved because it is too large." : warning + " Image was not saved because it is too large.";
            }

            menuItemRepo.insertMenuItem(
                    uid,
                    input.getName(),
                    description,
                    input.getPrice(),
                    image,
                    input.getIsAvailable() != null ? input.getIsAvailable() : true,
                    category.getId(),
                    restaurant.getId()
            );

            MenuItem savedMenuItem = menuItemRepo.findByUid(uid)
                    .orElse(null);
            if (savedMenuItem == null) {
                return ResponseList.error("Menu item not found after insert");
            }

            String message = warning.isBlank() ? "Menu items saved successfully" : "Menu items saved successfully. " + warning;
            return new ResponseList<>(ResponseStatus.Success, List.of(mapToDto(savedMenuItem)), message);
        } catch (Exception e) {
            return ResponseList.error(e.getMessage() != null ? e.getMessage() : "Failed to create menu item");
        }

    }

    public Response<MenuItemDto> updateMenuItem(String uid, MenuItemDto input) {

        try {
            if (uid == null || uid.isBlank()) {
                return new Response<>("uid is required", ResponseStatus.Error);
            }
            if (input == null) {
                return new Response<>("Input is required", ResponseStatus.Error);
            }

            String description = input.getDescription();
            String image = input.getImage();
            String warning = "";
            if (description != null && description.length() > 255) {
                description = description.substring(0, 255);
                warning = "Description was truncated to 255 characters.";
            }
            if (image != null && image.length() > 255) {
                image = null;
                warning = warning.isBlank() ? "Image was not saved because it is too large." : warning + " Image was not saved because it is too large.";
            }

            int update = menuItemRepo.updateMenuItem(
                    uid,
                    input.getName(),
                    description,
                    input.getPrice(),
                    image,
                    input.getIsAvailable() != null ? input.getIsAvailable() : true);
            if (update == 0) {
                return new Response<>("Menu item not found or no changes made", ResponseStatus.Error);
            }

            MenuItem updatedMenuItem = menuItemRepo.findByUid(uid)
                    .orElse(null);
            if (updatedMenuItem == null) {
                return new Response<>("Menu item not found", ResponseStatus.Error);
            }

            String message = warning.isBlank() ? "Menu item updated successfully" : "Menu item updated successfully. " + warning;
            return new Response<>(ResponseStatus.Success, mapToDto(updatedMenuItem), message);
        } catch (Exception e) {
            return new Response<>(e.getMessage() != null ? e.getMessage() : "Failed to update menu item", ResponseStatus.Error);
        }
    }

    public Response<String> deleteMenuItem(String uid) {

        int deleted = menuItemRepo.softDeleteMenuItem(uid);

        if (deleted == 0) {
            throw new RuntimeException("Menu item not found or already deleted");
        }

        return new Response<>(ResponseStatus.Success, null, "Menu item deleted successfully");
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
