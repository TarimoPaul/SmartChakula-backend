package com.SmartChakula.MenuItem.Controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.SmartChakula.MenuItem.Dtos.MenuItemDto;
import com.SmartChakula.MenuItem.Services.MenuItemService;
import com.SmartChakula.Utils.GraphQlListResponse;
import com.SmartChakula.Utils.GraphQlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MenuItemController {

    private final MenuItemService menuItemService;

    @QueryMapping
    public GraphQlListResponse<MenuItemDto> getAllMenuItems() {
        return menuItemService.getAllMenuItems();
    }

    @QueryMapping
    public GraphQlListResponse<MenuItemDto> getMenuItemsByCategory(@Argument String categoryUid) {
        return menuItemService.getMenuItemsByCategory(categoryUid);
    }

    @QueryMapping
    public GraphQlListResponse<MenuItemDto> getMenuItemsByRestaurant(@Argument String restaurantUid) {
        return menuItemService.getMenuItemsByRestaurant(restaurantUid);
    }

    @QueryMapping
    public GraphQlResponse<MenuItemDto> getMenuItem(@Argument String uid) {
        return menuItemService.getMenuItem(uid);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public GraphQlResponse<MenuItemDto> saveMenuItem(@Argument MenuItemDto input, Authentication authentication) {
        return menuItemService.saveMenuItem(authentication.getName(), input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public GraphQlResponse<MenuItemDto> updateMenuItem(@Argument String uid, @Argument MenuItemDto input,
            Authentication authentication) {
        return menuItemService.updateMenuItem(authentication.getName(), uid, input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public GraphQlResponse<MenuItemDto> deleteMenuItem(@Argument String uid, Authentication authentication) {
        return menuItemService.deleteMenuItem(authentication.getName(), uid);
    }
}