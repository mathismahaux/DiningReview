package com.example.diningReview.repository;

import com.example.diningReview.model.DiningReview;
import com.example.diningReview.model.DiningReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiningReviewRepository extends JpaRepository<DiningReview, Long> {
    List<DiningReview> findByStatus(DiningReviewStatus status);
    List<DiningReview> findByRestaurantIdAndStatus(Long restaurantId, DiningReviewStatus status);
}