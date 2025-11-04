package com.Star_Bank.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class RecommendationResponse {
    @JsonProperty("user_id")
    private UUID userId;

    private List<Recommendation> recommendations;

    public RecommendationResponse() {}

    public RecommendationResponse(UUID userId, List<Recommendation> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
    }

    // Геттеры и сеттеры
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public List<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
