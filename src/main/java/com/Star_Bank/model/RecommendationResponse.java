package com.Star_Bank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Модель ответа API рекомендаций
 */
public class RecommendationResponse {
    @JsonProperty("user_id")
    private String userId;

    private List<Recommendation> recommendations;

    public RecommendationResponse(UUID userId, List<Recommendation> recommendations) {
        this.recommendations = new ArrayList<>();
    }

    public RecommendationResponse(String userId, List<Recommendation> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}