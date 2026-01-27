package com.SmartChakula.Category.Controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.SmartChakula.Category.Dtos.CategoryDto;
import com.SmartChakula.Category.Services.CategoryService;
import com.SmartChakula.Utils.Response;
import com.SmartChakula.Utils.ResponseList;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;


    @QueryMapping
    public ResponseList<CategoryDto> getCategoryService() {
        return categoryService.getCategoriesByRestaurants(null);
    }

    @QueryMapping
    public Response<CategoryDto> getCategoryByUid(@Argument String uid) {
        return categoryService.getCategoryByUid(uid);
    }

    @MutationMapping
    public Response<CategoryDto> createCategory(@Argument CategoryDto input) {
        return categoryService.createCategory(input);
    }

    @MutationMapping
    public Response<CategoryDto> updateCategory(@Argument CategoryDto input) {
        return categoryService.updateCategory(input);
    }

    @MutationMapping
    public Response<String> deleteCategory(@Argument String uid) {
        return categoryService.deleteCategory(uid);
    }
}
