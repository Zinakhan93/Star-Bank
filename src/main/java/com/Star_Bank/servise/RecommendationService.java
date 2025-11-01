package com.Star_Bank.servise;

import com.Star_Bank.DTO.RecommendationDto;
import com.Star_Bank.DTO.RecommendationResponse;
import com.Star_Bank.recommendation.RecommendationRuleSet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class RecommendationService {
    private final List<RecommendationRuleSet> ruleSets;

    public RecommendationService(List<RecommendationRuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public RecommendationResponse getRecommendations(UUID userId) {
        List<RecommendationDto> recommendations = ruleSets.stream()
                .map(rule -> rule.check(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return new RecommendationResponse(userId, recommendations);
    }
}
