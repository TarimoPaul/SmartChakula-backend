package com.SmartChakula.Uaa.User.Entity;

import com.SmartChakula.Restaurant.Entity.RestaurantEntity;
import com.SmartChakula.Utils.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
  uniqueConstraints = @UniqueConstraint(
    columnNames = {"user_uid", "restaurant_uid"}
  )
)

public class UserRestaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_uid", referencedColumnName = "uid")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "restaurant_uid", referencedColumnName = "uid")
    private RestaurantEntity restaurant;

    @Enumerated(EnumType.STRING)
    private UserRole role;

   
}
