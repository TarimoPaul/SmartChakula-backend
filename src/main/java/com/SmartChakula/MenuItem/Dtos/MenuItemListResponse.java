package com.SmartChakula.MenuItem.Dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemListResponse {
    private String status;
    private String message;
    private List<MenuItemDto> data;
}