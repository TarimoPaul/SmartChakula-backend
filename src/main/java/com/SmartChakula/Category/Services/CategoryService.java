package com.SmartChakula.Category.Services;

import com.SmartChakula.Category.Dtos.CategoryDto;
import com.SmartChakula.Category.Entity.CategoryEntity;
import com.SmartChakula.Category.Repository.CategoryRepo;
import com.SmartChakula.Restaurant.Entity.RestaurantEntity;
import com.SmartChakula.Restaurant.Repository.RestaurantRepo;
import com.SmartChakula.Uaa.User.Entity.UserEntity;
import com.SmartChakula.Uaa.User.Entity.UserRole;
import com.SmartChakula.Uaa.User.Repository.UserRepo;
import com.SmartChakula.Uaa.User.Repository.UserRestaurantRepo;
import com.SmartChakula.Utils.GraphQlListResponse;
import com.SmartChakula.Utils.GraphQlResponse;
import com.SmartChakula.Utils.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final RestaurantRepo restaurantRepo;
    private final UserRestaurantRepo userRestaurantRepo;
    private final UserRepo userRepo;

    public GraphQlListResponse<CategoryDto> getAllCategories() {
        try {
            log.info("Fetching all categories");
            List<CategoryEntity> categories = categoryRepo.findAll();
            List<CategoryDto> dtoList = categories.stream()
                    .map(this::mapToDto)
                    .toList();

            return new GraphQlListResponse<>(
                    ResponseStatus.Success.toString(),
                    "Categories fetched successfully",
                    dtoList);
        } catch (Exception e) {
            log.error("Error fetching categories: {}", e.getMessage(), e);
            return new GraphQlListResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to fetch categories: " + e.getMessage(),
                    null);
        }
    }

    public GraphQlListResponse<CategoryDto> getCategoriesByRestaurants(String restaurantUid) {
        try {
            log.info("Fetching categories for restaurant: {}", restaurantUid);

            List<CategoryEntity> categories;
            if (restaurantUid == null || restaurantUid.isBlank()) {
                categories = categoryRepo.findAll();
            } else {
                categories = categoryRepo.findByRestaurantUid(restaurantUid);
            }

            List<CategoryDto> dtoList = categories.stream()
                    .map(this::mapToDto)
                    .toList();

            return new GraphQlListResponse<>(
                    ResponseStatus.Success.toString(),
                    "Categories fetched successfully",
                    dtoList);
        } catch (Exception e) {
            log.error("Error fetching categories by restaurant: {}", e.getMessage(), e);
            return new GraphQlListResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to fetch categories: " + e.getMessage(),
                    null);
        }
    }

    public GraphQlResponse<CategoryDto> getCategoryByUid(String uid) {
        try {
            log.info("Fetching category with uid: {}", uid);

            if (uid == null || uid.isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "UID is required",
                        null);
            }

            CategoryEntity category = categoryRepo.findByUid(uid).orElse(null);

            if (category == null) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Category not found",
                        null);
            }

            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Category fetched successfully",
                    mapToDto(category));
        } catch (Exception e) {
            log.error("Error fetching category: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to fetch category: " + e.getMessage(),
                    null);
        }
    }

    @Transactional
    public GraphQlResponse<CategoryDto> createCategory(String email, CategoryDto input) {
        try {
            log.info("Creating category with name: {}", input.getName());

            if (input == null) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Input is required",
                        null);
            }

            // Validate required fields
            if (input.getName() == null || input.getName().isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Category name is required",
                        null);
            }

            if (input.getRestaurantUid() == null || input.getRestaurantUid().isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Restaurant UID is required",
                        null);
            }

            // Check if restaurant exists
            RestaurantEntity restaurant = restaurantRepo.findByUid(input.getRestaurantUid()).orElse(null);
            if (restaurant == null) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Restaurant not found with UID: " + input.getRestaurantUid(),
                        null);
            }

            UserEntity user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not Found"));

            assertCanManageRestaurant(user, input.getRestaurantUid()

            );

            // Create category
            CategoryEntity category = new CategoryEntity();
            category.setUid(UUID.randomUUID().toString());
            category.setName(input.getName().trim());
            category.setDescription(input.getDescription());
            category.setRestaurant(restaurant);
            category.setIsActive(true);

            CategoryEntity savedCategory = categoryRepo.save(category);
            log.info("Category created with uid: {}", savedCategory.getUid());

            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Category created successfully",
                    mapToDto(savedCategory));
        } catch (Exception e) {
            log.error("Error creating category: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to create category: " + e.getMessage(),
                    null);
        }
    }

    @Transactional
    public GraphQlResponse<CategoryDto> updateCategory(String email, CategoryDto input) {
        try {
            log.info("Updating category with uid: {}", input.getUid());

            if (input == null || input.getUid() == null || input.getUid().isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Category UID is required",
                        null);
            }

            if (input.getName() == null || input.getName().isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Category name is required",
                        null);
            }

            // Find existing category
            CategoryEntity category = categoryRepo.findByUid(input.getUid()).orElse(null);
            if (category == null) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Category not found",
                        null);
            }

            // Update fields
            category.setName(input.getName().trim());
            category.setDescription(input.getDescription());

            UserEntity user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            assertCanManageRestaurant(
                    user,
                    category.getRestaurant().getUid());

            CategoryEntity updatedCategory = categoryRepo.save(category);
            log.info("Category updated with uid: {}", updatedCategory.getUid());

            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Category updated successfully",
                    mapToDto(updatedCategory));
        } catch (Exception e) {
            log.error("Error updating category: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to update category: " + e.getMessage(),
                    null);
        }
    }

    @Transactional
    public GraphQlResponse<CategoryDto> deleteCategory(String email, String uid) {
        try {
            log.info("Deleting category with uid: {}", uid);

            if (uid == null || uid.isBlank()) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "UID is required",
                        null);
            }

            CategoryEntity category = categoryRepo.findByUid(uid).orElse(null);
            if (category == null) {
                return new GraphQlResponse<>(
                        ResponseStatus.Error.toString(),
                        "Category not found",
                        null);
            }

            UserEntity user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            assertCanManageRestaurant(
                    user,
                    category.getRestaurant().getUid());

            // Soft delete
            category.setIsActive(false);
            categoryRepo.save(category);
            log.info("Category deleted with uid: {}", uid);

            return new GraphQlResponse<>(
                    ResponseStatus.Success.toString(),
                    "Category deleted successfully",
                    null);
        } catch (Exception e) {
            log.error("Error deleting category: {}", e.getMessage(), e);
            return new GraphQlResponse<>(
                    ResponseStatus.Error.toString(),
                    "Failed to delete category: " + e.getMessage(),
                    null);
        }
    }

    private CategoryDto mapToDto(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }

        CategoryDto dto = new CategoryDto();
        dto.setUid(entity.getUid());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setRestaurantUid(entity.getRestaurant().getUid());
        dto.setIsActive(entity.getIsActive());

        return dto;
    }

    private void assertCanManageRestaurant(UserEntity user, String restaurantUid) {

        // ADMIN → bypass
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        // OWNER → lazima awe owner wa restaurant
        if (user.getRole() == UserRole.OWNER) {
            boolean owns = restaurantRepo
                    .findByUid(restaurantUid)
                    .map(r -> r.getOwner().getUid().equals(user.getUid()))
                    .orElse(false);

            if (!owns) {
                throw new AccessDeniedException("You do not own this restaurant");
            }
            return;
        }

        // MANAGER → lazima awe assigned
        boolean assigned = userRestaurantRepo
                .existsByUserUidAndRestaurantUid(
                        user.getUid(),
                        restaurantUid);

        if (!assigned) {
            throw new AccessDeniedException(
                    "You are not assigned to this restaurant");
        }
    }

}