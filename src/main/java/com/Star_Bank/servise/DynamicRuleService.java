package com.Star_Bank.servise;

import com.Star_Bank.model.*;
import com.Star_Bank.repository.DynamicRuleRepository;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для управления динамическими правилами рекомендаций
 * Обеспечивает создание, удаление и получение правил
 */

    @Service
    public class DynamicRuleService {

        private static final Logger logger = LoggerFactory.getLogger(DynamicRuleService.class);

        private final DynamicRuleRepository dynamicRuleRepository;
        private final DynamicRuleEvaluator ruleEvaluator;
        private final ObjectMapper objectMapper;

        @Autowired
        public DynamicRuleService(DynamicRuleRepository dynamicRuleRepository,
                                  DynamicRuleEvaluator ruleEvaluator,
                                  ObjectMapper objectMapper) {
            this.dynamicRuleRepository = dynamicRuleRepository;
            this.ruleEvaluator = ruleEvaluator;
            this.objectMapper = objectMapper;
        }

        /**
         * Создает новое динамическое правило
         *
         * @param request DTO с данными для создания правила
         * @return созданное правило
         * @throws JsonProcessingException если ошибка сериализации JSON
         */
        @Transactional
        public RuleResponse createRule(CreateRuleRequest request) throws JsonProcessingException {
            // Сериализуем правило в JSON для хранения в БД
            String ruleJson = objectMapper.writeValueAsString(request.getRule());

            // Создаем сущность правила
            DynamicRule rule = new DynamicRule(
                    request.getProductName(),
                    request.getProductId(),
                    request.getProductText(),
                    ruleJson
            );

            // Сохраняем в БД
            DynamicRule savedRule = dynamicRuleRepository.save(rule);
            logger.info("Created dynamic rule for product: {}", request.getProductName());

            // Возвращаем DTO ответа
            return new RuleResponse(
                    savedRule.getId(),
                    savedRule.getProductName(),
                    savedRule.getProductId(),
                    savedRule.getProductText(),
                    request.getRule()
            );
        }

        /**
         * Удаляет правило по productId
         *
         * @param productId ID продукта правила
         * @return true если правило было удалено
         */
        @Transactional
        public boolean deleteRule(String productId) {
            Optional<DynamicRule> rule = dynamicRuleRepository.findByProductId(productId);
            if (rule.isPresent()) {
                dynamicRuleRepository.delete(rule.get());
                logger.info("Deleted dynamic rule for product ID: {}", productId);
                return true;
            }
            return false;
        }

        /**
         * Получает все динамические правила
         *
         * @return список всех правил
         */
        public RuleListResponse getAllRules() {
            List<DynamicRule> rules = dynamicRuleRepository.findAll();

            List<RuleResponse> ruleResponses = rules.stream()
                    .map(this::convertToRuleResponse)
                    .collect(Collectors.toList());

            return new RuleListResponse(ruleResponses);
        }

        /**
         * Получает все динамические правила для оценки рекомендаций
         *
         * @return список сущностей динамических правил
         */
        public List<DynamicRule> getAllRulesForEvaluation() {
            return dynamicRuleRepository.findAll();
        }

        /**
         * Конвертирует сущность DynamicRule в DTO RuleResponse
         */
        private RuleResponse convertToRuleResponse(DynamicRule rule) {
            try {
                List<RuleQuery> queries = objectMapper.readValue(
                        rule.getRuleQueries(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<RuleQuery>>() {
                        }
                );

                return new RuleResponse(
                        rule.getId(),
                        rule.getProductName(),
                        rule.getProductId(),
                        rule.getProductText(),
                        queries
                );

            } catch (JsonProcessingException e) {
                logger.error("Error converting rule to response for rule ID {}: {}", rule.getId(), e.getMessage());
                throw new RuntimeException("Error processing rule data", e);
            }
        }
    }


