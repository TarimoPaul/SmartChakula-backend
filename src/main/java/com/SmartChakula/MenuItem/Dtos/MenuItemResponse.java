package com.SmartChakula.MenuItem.Dtos;


import com.SmartChakula.Utils.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private String status;      // "Success", "Error", etc
    private String message;
    private MenuItemDto data;
}