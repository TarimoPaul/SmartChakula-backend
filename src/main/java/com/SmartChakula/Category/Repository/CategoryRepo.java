package com.SmartChakula.Category.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SmartChakula.Category.Entity.CategoryEntity;

import jakarta.transaction.Transactional;

@Repository
public interface CategoryRepo extends JpaRepository<CategoryEntity, Long> {


   @Query(value = """
           SELECT * FROM categories where uid = :uid AND is_active = true AND is_deleted = false
           """, nativeQuery = true)
    Optional<CategoryEntity> findByUid(@Param("uid") String uid);


    @Query(value = """
            SELECT c.* FROM categories c 
            JOIN restaurants r ON r.id = c.restaurant_id 
            WHERE r.uid = :restaurantUid
            AND c.is_active = true
            ORDER BY c.created_at ASC
            """, nativeQuery = true)
    List<CategoryEntity> findByRestaurantUid(@Param("restaurantUid")String restaurantUid);

    @Query(value = """
            SELECT c.* FROM categories c
            WHERE c.is_active = true AND c.is_deleted = false
            ORDER BY c.created_at ASC
            """, nativeQuery = true)
    List<CategoryEntity> findAllActive();


    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO categories (uid, name, description, restaurant_id, is_active, is_deleted, created_at, updated_at)
        VALUES (:uid, :name, :description, :restaurantId, true, false, NOW(), NOW())
        """, nativeQuery = true)
    void insertCategory(
        @Param("uid") String uid,
        @Param("name") String name,
        @Param("description") String description,
        @Param("restaurantId") Long restaurantId
    );

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE categories
            SET name = :name, description = :description, updated_at = NOW()
            WHERE uid = :uid
            AND is_active = true AND is_deleted = false
            """, nativeQuery = true)
    int updateCategory(
        @Param("uid") String uid,
        @Param("name") String name,
        @Param("description") String description
    );


    @Modifying
    @Transactional
    @Query(value = """
        UPDATE categories
        SET is_active = false, is_deleted = true, updated_at = NOW()
        WHERE uid = :uid
        AND is_active = true AND is_deleted = false
        """, nativeQuery = true)
    int softDeleteCategory(
        @Param("uid") String uid
    );
    
} 