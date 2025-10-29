package com.Star_Bank.recommendation;

import com.Star_Bank.DTO.RecommendationDto;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<RecommendationDto> check(UUID userId);
}
