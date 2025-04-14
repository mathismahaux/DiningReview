package com.example.diningReview.controller;

import com.example.diningReview.model.Restaurant;
import com.example.diningReview.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping
    public ResponseEntity<?> createRestaurant(@RequestBody Restaurant restaurant) {
        if (restaurantRepository.existsByNameAndAddress(restaurant.getName(), restaurant.getAddress())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Restaurant already exists.");
        }
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRestaurant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurant(@PathVariable Long id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if (restaurant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurant not found.");
        }

        Restaurant foundRestaurant = restaurant.get();
        if (foundRestaurant.getPeanutScore() != null) {
            foundRestaurant.setPeanutScore(Double.valueOf(decimalFormat.format(foundRestaurant.getPeanutScore())));
        }
        if (foundRestaurant.getEggScore() != null) {
            foundRestaurant.setEggScore(Double.valueOf(decimalFormat.format(foundRestaurant.getEggScore())));
        }
        if (foundRestaurant.getDairyScore() != null) {
            foundRestaurant.setDairyScore(Double.valueOf(decimalFormat.format(foundRestaurant.getDairyScore())));
        }
        if (foundRestaurant.getOverallScore() != null) {
            foundRestaurant.setOverallScore(Double.valueOf(decimalFormat.format(foundRestaurant.getOverallScore())));
        }

        return ResponseEntity.ok(foundRestaurant);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Restaurant>> searchRestaurants(
            @RequestParam String zipcode,
            @RequestParam(required = false) String allergy) {
        List<Restaurant> restaurants;
    
        switch (allergy != null ? allergy.toLowerCase() : "") {
            case "peanut":
                restaurants = restaurantRepository.findByZipcodeAndPeanutScoreNotNullOrderByOverallScoreDesc(zipcode);
                break;
            case "egg":
                restaurants = restaurantRepository.findByZipcodeAndEggScoreNotNullOrderByOverallScoreDesc(zipcode);
                break;
            case "dairy":
                restaurants = restaurantRepository.findByZipcodeAndDairyScoreNotNullOrderByOverallScoreDesc(zipcode);
                break;
            default:
                return ResponseEntity.badRequest().body(null);
        }
    
        return ResponseEntity.ok(restaurants);
    }
}