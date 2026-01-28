package com.SmartChakula.MenuItem.Controller;

import org.springframework.data.jpa.repository.Query;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.SmartChakula.MenuItem.Dtos.MenuItemDto;
import com.SmartChakula.MenuItem.Services.MenuItemService;
import com.SmartChakula.Utils.Response;
import com.SmartChakula.Utils.ResponseList;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;
    

    @QueryMapping
    public ResponseList<MenuItemDto> getMenuItemsByCategory(@Argument String categoryUid) {
        return menuItemService.getMenuItemsByCategory(categoryUid);
    }


    @QueryMapping
    public ResponseList<MenuItemDto> getMenuItemsByRestaurant(@Argument String restaurantUid) {
        return menuItemService.getMenuItemsByRestaurant(restaurantUid);
    }

    @QueryMapping
    public ResponseList<MenuItemDto> getAllMenuItems() {
        return menuItemService.getAllMenuItems();
    }


    @MutationMapping
    public ResponseList<MenuItemDto> createMenuItem(@Argument MenuItemDto input) {
        return menuItemService.saveMenuItems(input);
    }
    
    @MutationMapping
    public Response<MenuItemDto> updateMenuItem(@Argument String uid, @Argument MenuItemDto input) {
        return menuItemService.updateMenuItem(uid, input);

    }

    @MutationMapping
    public Response<String> deleteMenuItem(@Argument String uid) {
        return menuItemService.deleteMenuItem(uid); 
    }

}
