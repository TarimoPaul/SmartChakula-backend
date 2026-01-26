package com.SmartChakula.Uaa.User.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SmartChakula.Restaurant.Entity.RestaurantEntity;
import com.SmartChakula.Uaa.User.Entity.UserEntity;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhone(String phone);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :identifier OR u.phone = :identifier  ")
    Optional<UserEntity> findByIdentifier(@Param("identifier") String identifier);


    boolean existsByEmail(String email);
    
    Optional<UserEntity> findByUid(String uid);    


}
