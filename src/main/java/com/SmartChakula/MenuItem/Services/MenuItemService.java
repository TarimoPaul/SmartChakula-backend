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

    public ResponseList<MenuItemDto> getAllMenuItems() {

        List<MenuItem> menuItems = menuItemRepo.findAllActive();

        List<MenuItemDto> menuItemDtos = menuItems.stream()
                .map(this::mapToDto)
                .toList();

        return ResponseList.success(menuItemDtos, "Menu items fetched successfully");
    }

    public ResponseList<MenuItemDto> saveMenuItems(MenuItemDto input) {

        CategoryEntity category = categoryRepo.findByUid(input.getCategoryUid())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        RestaurantEntity restaurant = restaurantRepo.findByUid(input.getRestaurantUid())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        String uid = java.util.UUID.randomUUID().toString();

        menuItemRepo.insertMenuItem(
                uid,
                input.getName(),
                input.getDescription(),
                input.getPrice(),
                input.getImage(),
                input.getIsAvailable() != null ? input.getIsAvailable() : true,
                category.getId(), // FAST JOIN
                restaurant.getId() // FAST JOIN
        );

        MenuItem savedMenuItem = menuItemRepo.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        return new ResponseList<>(ResponseStatus.Success, List.of(mapToDto(savedMenuItem)),
                "Menu items saved successfully");

    }

    public Response<MenuItemDto> updateMenuItem(String uid, MenuItemDto input) {

        int update = menuItemRepo.updateMenuItem(
                uid,
                input.getName(),
                input.getDescription(),
                input.getPrice(),
                input.getImage(),
                input.getIsAvailable() != null ? input.getIsAvailable() : true);
        if (update == 0) {
            throw new RuntimeException("Menu item not found or no changes made");
        }

        MenuItem updatedMenuItem = menuItemRepo.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        return new Response<>(ResponseStatus.Success, mapToDto(updatedMenuItem), "Menu item updated successfully");
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
