package com.SmartChakula.Category.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryListResponse {
    private String status;
    private String message;
    private java.util.List<CategoryDto> data;
}