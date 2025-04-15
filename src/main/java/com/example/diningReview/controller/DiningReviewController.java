package com.example.diningReview.controller;

import com.example.diningReview.model.DiningReview;
import com.example.diningReview.model.DiningReviewStatus;
import com.example.diningReview.model.Restaurant;
import com.example.diningReview.repository.DiningReviewRepository;
import com.example.diningReview.repository.RestaurantRepository;
import com.example.diningReview.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dining-reviews")
public class DiningReviewController {

    private final DiningReviewRepository diningReviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public DiningReviewController(DiningReviewRepository diningReviewRepository,
                                  RestaurantRepository restaurantRepository,
                                  UserRepository userRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> submitDiningReview(@RequestBody DiningReview diningReview) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(diningReview.getRestaurantId());
        if (restaurant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Restaurant does not exist.");
        }

        if (!userRepository.existsByDisplayName(diningReview.getSubmittedBy())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User does not exist.");
        }

        diningReview.setStatus(DiningReviewStatus.PENDING);
        DiningReview savedReview = diningReviewRepository.save(diningReview);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<DiningReview>> getPendingReviews() {
        List<DiningReview> pendingReviews = diningReviewRepository.findByStatus(DiningReviewStatus.PENDING);
        return ResponseEntity.ok(pendingReviews);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateReviewStatus(@PathVariable Long id, @RequestBody DiningReviewStatus status) {
        Optional<DiningReview> existingReview = diningReviewRepository.findById(id);
        if (existingReview.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dining review not found.");
        }

        DiningReview review = existingReview.get();
        review.setStatus(status);
        diningReviewRepository.save(review);

        if (status == DiningReviewStatus.ACCEPTED) {
            updateRestaurantScores(review.getRestaurantId());
        }

        return ResponseEntity.ok(review);
    }

    private void updateRestaurantScores(Long restaurantId) {
        List<DiningReview> approvedReviews = diningReviewRepository.findByRestaurantIdAndStatus(restaurantId, DiningReviewStatus.ACCEPTED);

        double peanutScoreSum = 0, eggScoreSum = 0, dairyScoreSum = 0;
        int peanutCount = 0, eggCount = 0, dairyCount = 0;

        for (DiningReview review : approvedReviews) {
            if (review.getPeanutScore() != null) {
                peanutScoreSum += review.getPeanutScore();
                peanutCount++;
            }
            if (review.getEggScore() != null) {
                eggScoreSum += review.getEggScore();
                eggCount++;
            }
            if (review.getDairyScore() != null) {
                dairyScoreSum += review.getDairyScore();
                dairyCount++;
            }
        }

        double overallScoreSum = peanutScoreSum + eggScoreSum + dairyScoreSum;
        int overallCount = peanutCount + eggCount + dairyCount;

        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isPresent()) {
            Restaurant r = restaurant.get();
            r.setPeanutScore(peanutCount > 0 ? peanutScoreSum / peanutCount : null);
            r.setEggScore(eggCount > 0 ? eggScoreSum / eggCount : null);
            r.setDairyScore(dairyCount > 0 ? dairyScoreSum / dairyCount : null);
            r.setOverallScore(overallCount > 0 ? overallScoreSum / overallCount : null);
            restaurantRepository.save(r);
        }
    }
}