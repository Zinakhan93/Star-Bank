package com.Star_Bank.recommendation;

import com.Star_Bank.DTO.Recommendation;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRule {
    Optional<Recommendation> check(UUID userId);
}
