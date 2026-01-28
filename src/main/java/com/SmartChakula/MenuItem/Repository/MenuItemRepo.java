package com.SmartChakula.MenuItem.Repository;

import com.SmartChakula.MenuItem.Entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem, Long> {

    @Query(value = """
            SELECT * FROM menu_items
            WHERE uid = :uid
            AND is_active = true
            AND is_deleted = false
            """, nativeQuery = true)
    Optional<MenuItem> findByUid(@Param("uid") String uid);

    @Query(value = """
            SELECT mi.* FROM menu_items mi
            JOIN categories c ON c.id = mi.category_id
            WHERE c.uid = :categoryUid
            AND mi.is_active = true
            AND mi.is_deleted = false
            ORDER BY mi.created_at ASC
            """, nativeQuery = true)
    List<MenuItem> findByCategoryUid(@Param("categoryUid") String categoryUid);

    @Query(value = """
            SELECT mi.* FROM menu_items mi
            JOIN restaurants r ON r.id = mi.restaurant_id
            WHERE r.uid = :restaurantUid
            AND mi.is_active = true
            AND mi.is_deleted = false
            ORDER BY mi.created_at ASC
            """, nativeQuery = true)
    List<MenuItem> findByRestaurantUid(@Param("restaurantUid") String restaurantUid);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO menu_items
            (uid, name, description, price, image, is_available,
            category_id, restaurant_id, is_active, is_deleted, created_at, updated_at)
            VALUES (:uid, :name, :description, :price, :image, :available, :categoryId, :restaurantId, true, false, NOW(), NOW())
            """, nativeQuery = true)
    int insertMenuItem(@Param("uid") String uid,
            @Param("name") String name,
            @Param("description") String description,
            @Param("price") Double price,
            @Param("image") String image,
            @Param("available") boolean available,
            @Param("categoryId") Long categoryId,
            @Param("restaurantId") Long restaurantId);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE menu_items
            SET name = :name, description = :description, price = :price, image = :image,
                is_available = :available, updated_at = NOW()
            WHERE uid = :uid
            AND is_active = true AND is_deleted = false
            """, nativeQuery = true)
    int updateMenuItem(
            @Param("uid") String uid,
            @Param("name") String name,
            @Param("description") String description,
            @Param("price") Double price,
            @Param("image") String image,
            @Param("available") boolean available);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE menu_items
            SET is_active = false, is_deleted = true, updated_at = NOW()
            WHERE uid = :uid
            AND is_active = true AND is_deleted = false
            """, nativeQuery = true)
    int softDeleteMenuItem(@Param("uid") String uid);

    @Query(value = """
            SELECT * FROM menu_items
            WHERE is_active = true
            AND is_deleted = false
            ORDER BY created_at ASC
            """, nativeQuery = true)
    List<MenuItem> findAllActive();
}