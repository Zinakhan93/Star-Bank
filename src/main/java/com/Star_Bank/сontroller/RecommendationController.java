package com.Star_Bank.сontroller;

import com.Star_Bank.model.RecommendationResponse;
import com.Star_Bank.servise.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST контроллер для обработки запросов рекомендаций
 */
@RestController
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @PathVariable("user_id") String userId) {

        RecommendationResponse response = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(response);
    }

}
