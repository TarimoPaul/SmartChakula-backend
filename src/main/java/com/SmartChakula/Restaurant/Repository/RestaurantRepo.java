package com.SmartChakula.Restaurant.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SmartChakula.Restaurant.Entity.RestaurantEntity;

@Repository
public interface RestaurantRepo extends JpaRepository<RestaurantEntity, Long> {

    @Query("SELECT r FROM RestaurantEntity r JOIN FETCH r.owner WHERE r.uid = :uid")
    Optional<RestaurantEntity> findByUid(@Param("uid") String uid);

    @Query("SELECT r FROM RestaurantEntity r JOIN FETCH r.owner WHERE r.isActive = true AND r.isDeleted = false")
    List<RestaurantEntity> findByIsActive();

}
