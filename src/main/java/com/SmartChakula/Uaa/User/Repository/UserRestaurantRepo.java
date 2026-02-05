package com.SmartChakula.Uaa.User.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SmartChakula.Uaa.User.Entity.UserRestaurant;

@Repository
public interface UserRestaurantRepo extends JpaRepository<UserRestaurant, Long> {

    // 🔐 Permission check (manager/owner assigned to restaurant)
    @Query("""
                SELECT ur FROM UserRestaurant ur
                JOIN FETCH ur.user u
                JOIN FETCH ur.restaurant r
                WHERE u.uid = :userUid
                  AND r.uid = :restaurantUid
            """)
    Optional<UserRestaurant> findByUserUidAndRestaurantUid(
            @Param("userUid") String userUid,
            @Param("restaurantUid") String restaurantUid);

    // 📋 Restaurants zote alizo-assigniwa user (MANAGER / OWNER)
    @Query("""
                SELECT ur FROM UserRestaurant ur
                JOIN FETCH ur.restaurant r
                WHERE ur.user.uid = :userUid
            """)
    List<UserRestaurant> findAllByUserUid(
            @Param("userUid") String userUid);

    // 👥 Staff wote wa restaurant (MANAGERS / OWNER)
    @Query("""
                SELECT ur FROM UserRestaurant ur
                JOIN FETCH ur.user u
                WHERE ur.restaurant.uid = :restaurantUid
            """)
    List<UserRestaurant> findAllByRestaurantUid(
            @Param("restaurantUid") String restaurantUid);

    // ⚡ Fast boolean permission check
    @Query("""
                SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END
                FROM UserRestaurant ur
                WHERE ur.user.uid = :userUid
                  AND ur.restaurant.uid = :restaurantUid
            """)
    boolean existsByUserUidAndRestaurantUid(
            @Param("userUid") String userUid,
            @Param("restaurantUid") String restaurantUid);
}
