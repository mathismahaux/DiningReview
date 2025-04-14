package com.example.diningReview.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dining_reviews")
public class DiningReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String submittedBy;
    private Long restaurantId;

    private Integer peanutScore;
    private Integer eggScore;
    private Integer dairyScore;

    private String commentary;

    @Enumerated(EnumType.STRING)
    private DiningReviewStatus status = DiningReviewStatus.PENDING;
}