package com.Star_Bank.servise;
import com.Star_Bank.DTO.Recommendation;
import com.Star_Bank.DTO.RecommendationResponse;
import com.Star_Bank.model.DynamicRule;
import com.Star_Bank.model.DynamicRuleEvaluator;
import com.Star_Bank.recommendation.RecommendationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;




@Service
public class RecommendationService {
    private final List<RecommendationRule> staticRules;
    private final DynamicRuleService dynamicRuleService;
    private final DynamicRuleEvaluator dynamicRuleEvaluator;

    @Autowired
    public RecommendationService(List<RecommendationRule> staticRules,
                                 DynamicRuleService dynamicRuleService,
                                 DynamicRuleEvaluator dynamicRuleEvaluator) {
        this.staticRules = staticRules;
        this.dynamicRuleService = dynamicRuleService;
        this.dynamicRuleEvaluator = dynamicRuleEvaluator;
    }

    /**
     * Получает рекомендации продуктов для указанного пользователя
     * Проверяет как статические правила, так и динамические правила из БД
     * @param userId ID пользователя
     * @return объект RecommendationResponse с рекомендациями
     */
    public RecommendationResponse getRecommendations(UUID userId) {
        List<Recommendation> recommendations = new ArrayList<>();

        // Проверяем статические правила (из первой итерации)
        List<Recommendation> staticRecommendations = staticRules.stream()
                .map(rule -> rule.check(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        recommendations.addAll(staticRecommendations);

        // Проверяем динамические правила из БД
        List<Recommendation> dynamicRecommendations = evaluateDynamicRules(userId);
        recommendations.addAll(dynamicRecommendations);

        return new RecommendationResponse(userId, recommendations);
    }

    /**
     * Оценивает все динамические правила для пользователя
     * @param userId ID пользователя
     * @return список рекомендаций из динамических правил
     */
    private List<Recommendation> evaluateDynamicRules(UUID userId) {
        List<DynamicRule> dynamicRules = dynamicRuleService.getAllRulesForEvaluation();

        return dynamicRules.stream()
                .map(rule -> dynamicRuleEvaluator.evaluateRule(
                        userId,
                        rule.getRuleQueries(),
                        rule.getProductName(),
                        rule.getProductId(),
                        rule.getProductText()
                ))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
