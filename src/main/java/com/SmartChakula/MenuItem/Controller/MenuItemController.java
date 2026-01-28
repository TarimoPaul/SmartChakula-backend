package com.SmartChakula.MenuItem.Controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.SmartChakula.MenuItem.Dtos.MenuItemDto;
import com.SmartChakula.MenuItem.Dtos.MenuItemResponse;
import com.SmartChakula.MenuItem.Dtos.MenuItemListResponse;
import com.SmartChakula.MenuItem.Services.MenuItemService;
import com.SmartChakula.Utils.Response;
import com.SmartChakula.Utils.ResponseList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MenuItemController {

    private final MenuItemService menuItemService;

    @QueryMapping
    public MenuItemListResponse getAllMenuItems() {
        log.info("getAllMenuItems query called");
        ResponseList<MenuItemDto> serviceResponse = menuItemService.getAllMenuItems();
        
        return new MenuItemListResponse(
            serviceResponse.getStatus().toString(),
            serviceResponse.getMessage(),
            serviceResponse.getData()
        );
    }

    @QueryMapping
    public MenuItemListResponse getMenuItemsByCategory(@Argument String categoryUid) {
        log.info("getMenuItemsByCategory query called with categoryUid: {}", categoryUid);
        ResponseList<MenuItemDto> serviceResponse = menuItemService.getMenuItemsByCategory(categoryUid);
        
        return new MenuItemListResponse(
            serviceResponse.getStatus().toString(),
            serviceResponse.getMessage(),
            serviceResponse.getData()
        );
    }

    @QueryMapping
    public MenuItemListResponse getMenuItemsByRestaurant(@Argument String restaurantUid) {
        log.info("getMenuItemsByRestaurant query called with restaurantUid: {}", restaurantUid);
        ResponseList<MenuItemDto> serviceResponse = menuItemService.getMenuItemsByRestaurant(restaurantUid);
        
        return new MenuItemListResponse(
            serviceResponse.getStatus().toString(),
            serviceResponse.getMessage(),
            serviceResponse.getData()
        );
    }

    @QueryMapping
    public MenuItemResponse getMenuItem(@Argument String uid) {
        log.info("getMenuItem query called with uid: {}", uid);
        Response<MenuItemDto> serviceResponse = menuItemService.getMenuItem(uid);
        
        return new MenuItemResponse(
            serviceResponse.getStatus().toString(),
            serviceResponse.getMessage(),
            serviceResponse.getData()
        );
    }

    @MutationMapping
    public MenuItemResponse saveMenuItem(@Argument MenuItemDto input) {
        log.info("saveMenuItem mutation called");
        Response<MenuItemDto> serviceResponse = menuItemService.saveMenuItem(input);
        
        return new MenuItemResponse(
            serviceResponse.getStatus().toString(),
            serviceResponse.getMessage(),
            serviceResponse.getData()
        );
    }

    @MutationMapping
    public MenuItemResponse updateMenuItem(@Argument String uid, @Argument MenuItemDto input) {
        log.info("updateMenuItem mutation called with uid: {}", uid);
        Response<MenuItemDto> serviceResponse = menuItemService.updateMenuItem(uid, input);
        
        return new MenuItemResponse(
            serviceResponse.getStatus().toString(),
            serviceResponse.getMessage(),
            serviceResponse.getData()
        );
    }

    @MutationMapping
    public MenuItemResponse deleteMenuItem(@Argument String uid) {
        log.info("deleteMenuItem mutation called with uid: {}", uid);
        Response<String> serviceResponse = menuItemService.deleteMenuItem(uid);
        
        return new MenuItemResponse(
            serviceResponse.getStatus().toString(),
            serviceResponse.getMessage(),
            null
        );
    }
}