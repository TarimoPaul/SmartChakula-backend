package com.SmartChakula.Category.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private String status;          // "Success", "Error", etc
    private String message;
    private CategoryDto data;       // Actual category
}
