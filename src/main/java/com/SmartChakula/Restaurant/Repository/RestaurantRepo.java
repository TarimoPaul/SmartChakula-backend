package com.SmartChakula.Restaurant.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SmartChakula.Restaurant.Entity.RestaurantEntity;

@Repository
public interface RestaurantRepo extends JpaRepository<RestaurantEntity, Long> {

    Optional<RestaurantEntity> findByUid(String uid);

    @Query("SELECT r FROM RestaurantEntity r WHERE r.isActive = true AND r.isDeleted = false")
    List<RestaurantEntity> findByIsActive();

}
