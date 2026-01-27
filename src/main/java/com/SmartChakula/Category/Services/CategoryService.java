package com.SmartChakula.Category.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.SmartChakula.Category.Dtos.CategoryDto;
import com.SmartChakula.Category.Entity.CategoryEntity;
import com.SmartChakula.Category.Repository.CategoryRepo;
import com.SmartChakula.Restaurant.Entity.RestaurantEntity;
import com.SmartChakula.Restaurant.Repository.RestaurantRepo;
import com.SmartChakula.Utils.Response;
import com.SmartChakula.Utils.ResponseList;
import com.SmartChakula.Utils.ResponseStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepo categoryRepo;

    private final RestaurantRepo restaurantRepo;


    public ResponseList<CategoryDto> getCategoriesByRestaurants(String restaurantUid) {
        List<CategoryEntity> categories = categoryRepo.findByRestaurantUid(restaurantUid);

        List<CategoryDto> dtoList = categories.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseList.success(dtoList, "Categories fetched successfully");
    }


    public Response<CategoryDto> getCategoryByUid(String uid) {
        CategoryEntity category = categoryRepo.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return new Response<>(ResponseStatus.Success, mapToResponse(category), "Category fetched successfully");
    }


    public Response<CategoryDto> createCategory(CategoryDto input) {

        RestaurantEntity restaurantEntity = restaurantRepo.findByUid(input.getRestaurantUid())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        String uid = java.util.UUID.randomUUID().toString();

        categoryRepo.insertCategory(
            uid, 
            input.getName(), 
            input.getDescription(), 
            restaurantEntity.getId()
            
        );

        CategoryEntity created = categoryRepo.findByUid(uid)
              .orElseThrow(() -> new RuntimeException("Category not found"));

        return new Response<>(ResponseStatus.Success, mapToResponse(created), "Category created successfully");

    }

    public Response<CategoryDto> updateCategory(CategoryDto input) {

        int updated = categoryRepo.updateCategory(
            input.getUid(),
            input.getName(),
            input.getDescription()
        );

        if (updated == 0) {
            throw new RuntimeException("Category not found or no changes made");
        }

        CategoryEntity updatedCategory = categoryRepo.findByUid(input.getUid())
                .orElseThrow();

        return new Response<>(ResponseStatus.Success, mapToResponse(updatedCategory), "Category updated successfully");
    }

    public Response<String> deleteCategory(String uid) {

        int delete = categoryRepo.softDeleteCategory(uid);

        if (delete == 0) {
            throw new RuntimeException("Category not found or already deleted");
            
        }
        
        return new Response<>(ResponseStatus.Success, null, "Category deleted successfully");
    }


    private CategoryDto mapToResponse(CategoryEntity category) {
        CategoryDto dto = new CategoryDto();
        dto.setUid(category.getUid());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setRestaurantUid(category.getRestaurant().getUid());
        dto.setIsActive(category.getIsActive().toString());
        return dto;
    }
}
