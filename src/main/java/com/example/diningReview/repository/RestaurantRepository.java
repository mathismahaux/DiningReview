package com.example.diningReview.repository;

import com.example.diningReview.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    boolean existsByNameAndAddress(String name, String address);
    List<Restaurant> findByAddressAndPeanutScoreNotNullOrEggScoreNotNullOrDairyScoreNotNullOrderByOverallScoreDesc(String address);
}