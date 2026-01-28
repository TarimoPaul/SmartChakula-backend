package com.SmartChakula.MenuItem.Entity;

import org.intellij.lang.annotations.JdkConstants.TabPlacement;

import com.SmartChakula.Category.Entity.CategoryEntity;
import com.SmartChakula.Restaurant.Entity.RestaurantEntity;
import com.SmartChakula.Utils.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu_items")
public class MenuItem extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double price;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(name =  "is_available", nullable = false)
    private boolean isAvailable = true;

    @ManyToOne(fetch =  FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;
  



    
}
