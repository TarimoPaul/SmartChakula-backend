package com.SmartChakula.Category.Controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.SmartChakula.Category.Dtos.CategoryDto;
import com.SmartChakula.Category.Dtos.CategoryInput;
import com.SmartChakula.Category.Dtos.CategoryResponse;
import com.SmartChakula.Category.Dtos.CategoryListResponse;
import com.SmartChakula.Category.Services.CategoryService;
import com.SmartChakula.Utils.Response;
import com.SmartChakula.Utils.ResponseList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @QueryMapping
    public CategoryListResponse getAllCategories() {
        log.info("getAllCategories query called");
        ResponseList<CategoryDto> serviceResponse = categoryService.getCategoriesByRestaurants(null);

        return new CategoryListResponse(
                serviceResponse.getStatus().toString(),
                serviceResponse.getMessage(),
                serviceResponse.getData());
    }

    @QueryMapping
    public CategoryResponse getCategoryByUid(@Argument String uid) {
        log.info("getCategoryByUid query called with uid: {}", uid);
        Response<CategoryDto> serviceResponse = categoryService.getCategoryByUid(uid);

        return new CategoryResponse(
                serviceResponse.getStatus().toString(),
                serviceResponse.getMessage(),
                serviceResponse.getData());
    }

    @MutationMapping
    public CategoryResponse saveCategory(@Argument CategoryInput input) {
        log.info("🔵 createCategory mutation called");
        log.info("🔵 Input: name={}, restaurantUid={}", input.getName(), input.getRestaurantUid());

        try {
            // Convert CategoryInput to CategoryDto
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setName(input.getName());
            categoryDto.setDescription(input.getDescription());
            categoryDto.setRestaurantUid(input.getRestaurantUid());

            Response<CategoryDto> serviceResponse = categoryService.createCategory(categoryDto);

            log.info("🔵 createCategory response: status={}", serviceResponse.getStatus());
            return new CategoryResponse(
                    serviceResponse.getStatus().toString(),
                    serviceResponse.getMessage(),
                    serviceResponse.getData());
        } catch (Exception e) {
            log.error("🔵 createCategory error: ", e);
            throw e;
        }
    }

    @MutationMapping
    public CategoryResponse updateCategory(@Argument CategoryInput input) {
        log.info("updateCategory mutation called");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setUid(input.getUid());
        categoryDto.setName(input.getName());
        categoryDto.setDescription(input.getDescription());
        categoryDto.setRestaurantUid(input.getRestaurantUid());

        Response<CategoryDto> serviceResponse = categoryService.updateCategory(categoryDto);

        return new CategoryResponse(
                serviceResponse.getStatus().toString(),
                serviceResponse.getMessage(),
                serviceResponse.getData());
    }

    @MutationMapping
    public CategoryResponse deleteCategory(@Argument String uid) {
        log.info("deleteCategory mutation called with uid: {}", uid);
        Response<String> serviceResponse = categoryService.deleteCategory(uid);

        return new CategoryResponse(
                serviceResponse.getStatus().toString(),
                serviceResponse.getMessage(),
                null);
    }
}