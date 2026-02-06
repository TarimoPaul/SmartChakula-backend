package com.SmartChakula.Category.Controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.SmartChakula.Category.Dtos.CategoryDto;
import com.SmartChakula.Category.Dtos.CategoryInput;
import com.SmartChakula.Category.Services.CategoryService;
import com.SmartChakula.Utils.GraphQlListResponse;
import com.SmartChakula.Utils.GraphQlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    // ✅ PUBLIC (read-only)
    @QueryMapping
    public GraphQlListResponse<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @QueryMapping
    public GraphQlResponse<CategoryDto> getCategoryByUid(@Argument String uid) {
        return categoryService.getCategoryByUid(uid);
    }

    // 🔐 CREATE
    @MutationMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public GraphQlResponse<CategoryDto> saveCategory(
            @Argument CategoryInput input,
            Authentication authentication) {

        CategoryDto dto = new CategoryDto();
        dto.setName(input.getName());
        dto.setDescription(input.getDescription());
        dto.setRestaurantUid(input.getRestaurantUid());

        return categoryService.createCategory(
                authentication.getName(), // email
                dto
        );
    }

    // 🔐 UPDATE
    @MutationMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public GraphQlResponse<CategoryDto> updateCategory(
            @Argument CategoryInput input,
            Authentication authentication) {

        CategoryDto dto = new CategoryDto();
        dto.setUid(input.getUid());
        dto.setName(input.getName());
        dto.setDescription(input.getDescription());
        dto.setRestaurantUid(input.getRestaurantUid());

        return categoryService.updateCategory(
                authentication.getName(),
                dto
        );
    }

    // 🔥 DELETE (HII ILIKUA HATARI)
    @MutationMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public GraphQlResponse<CategoryDto> deleteCategory(
            @Argument String uid, Authentication authentication) {

        return categoryService.deleteCategory(
                authentication.getName(),
                uid
        );
    }
}
