package com.Star_Bank.сontroller;

import com.Star_Bank.DTO.RecommendationResponse;
import com.Star_Bank.servise.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @PathVariable("user_id") UUID userId) {

        RecommendationResponse response = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(response);
    }
}
