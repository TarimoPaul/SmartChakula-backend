package com.SmartChakula.MenuItem.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemListResponse {

    private String status; // "Success", "Error", etc
    private String message;
    private java.util.List<MenuItemDto> data;

}
