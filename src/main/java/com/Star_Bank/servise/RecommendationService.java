package com.Star_Bank.servise;
import com.Star_Bank.model.Recommendation;
import com.Star_Bank.model.RecommendationResponse;
import com.Star_Bank.model.DynamicRule;
import com.Star_Bank.model.DynamicRuleEvaluator;
import com.Star_Bank.recommendation.RecommendationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Сервис рекомендаций с поддержкой статистики срабатываний
 */

@Service
public class RecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private final List<RecommendationRule> staticRules;
    private final DynamicRuleService dynamicRuleService;
    private final DynamicRuleEvaluator dynamicRuleEvaluator;
    private final StatisticService statisticService;

    @Autowired
    public RecommendationService(List<RecommendationRule> staticRules,
                                 DynamicRuleService dynamicRuleService,
                                 DynamicRuleEvaluator dynamicRuleEvaluator,
                                 StatisticService statisticService) {
        this.staticRules = staticRules;
        this.dynamicRuleService = dynamicRuleService;
        this.dynamicRuleEvaluator = dynamicRuleEvaluator;
        this.statisticService = statisticService;
    }

    public RecommendationResponse getRecommendations(String userId) {
        List<Recommendation> recommendations = new ArrayList<>();

        // 1. Статические правила
        List<Recommendation> staticRecommendations = staticRules.stream()
                .map(rule -> rule.check(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        recommendations.addAll(staticRecommendations);

        // 2. Динамические правила с регистрацией статистики
        List<Recommendation> dynamicRecommendations = evaluateDynamicRules(userId);
        recommendations.addAll(dynamicRecommendations);

        logger.info("Found {} recommendations for user {} ({} static, {} dynamic)",
                recommendations.size(), userId, staticRecommendations.size(), dynamicRecommendations.size());

        return new RecommendationResponse(userId, recommendations);
    }

    private List<Recommendation> evaluateDynamicRules(String userId) {
        List<DynamicRule> dynamicRules = dynamicRuleService.getAllRulesForEvaluation();

        return dynamicRules.stream()
                .map(rule -> {
                    Optional<Recommendation> recommendation = dynamicRuleEvaluator.evaluateRule(
                            userId,
                            rule.getRuleQueries(),
                            rule.getProductName(),
                            rule.getProductId(),
                            rule.getProductText()
                    );

                    // Регистрируем срабатывание в статистике
                    if (recommendation.isPresent()) {
                        statisticService.recordRuleHit(rule.getId(), rule.getProductName());
                        logger.debug("Rule {} triggered for user {}", rule.getId(), userId);
                    }

                    return recommendation;
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
