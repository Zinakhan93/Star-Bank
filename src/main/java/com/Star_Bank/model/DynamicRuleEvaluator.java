package com.Star_Bank.model;


import com.Star_Bank.repository.RecommendationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Компонент для оценки динамических правил
 * Выполняет проверку условий правил для конкретного пользователя
 */
public class DynamicRuleEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(DynamicRuleEvaluator.class);

    private final RecommendationRepository repository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DynamicRuleEvaluator(RecommendationRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Проверяет выполнение динамического правила для пользователя
     * @param userId ID пользователя
     * @param ruleJson JSON с массивом запросов правила
     * @param productName название продукта для рекомендации
     * @param productId ID продукта
     * @param productText описание продукта
     * @return Optional с рекомендацией, если правило выполняется
     */
    public Optional<Recommendation> evaluateRule(UUID userId, String ruleJson,
                                                 String productName, String productId, String productText) {
        try {
            // Десериализуем JSON с запросами правила
            List<RuleQuery> queries = objectMapper.readValue(ruleJson, new TypeReference<List<RuleQuery>>() {});

            // Проверяем все запросы правила
            boolean allQueriesPassed = queries.stream()
                    .allMatch(query -> evaluateQuery(userId, query));

            // Если все запросы прошли проверку, возвращаем рекомендацию
            if (allQueriesPassed) {
                return Optional.of(new Recommendation());
            }

        } catch (JsonProcessingException e) {
            logger.error("Error parsing rule JSON for product {}: {}", productId, e.getMessage());
        } catch (Exception e) {
            logger.error("Error evaluating rule for user {} and product {}: {}",
                    userId, productId, e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Выполняет оценку одного запроса правила
     * @param userId ID пользователя
     * @param query объект запроса
     * @return результат выполнения запроса (с учетом negate флага)
     */
    private boolean evaluateQuery(UUID userId, RuleQuery query) {
        boolean result = switch (query.getQuery()) {
            case "USER_OF" -> evaluateUserOfQuery(userId, query.getArguments());
            case "ACTIVE_USER_OF" -> evaluateActiveUserOfQuery(userId, query.getArguments());
            case "TRANSACTION_SUM_COMPARE" -> evaluateTransactionSumCompareQuery(userId, query.getArguments());
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> evaluateDepositWithdrawCompareQuery(userId, query.getArguments());
            default -> throw new IllegalArgumentException("Unknown query type: " + query.getQuery());
        };

        // Применяем отрицание если указано negate: true
        return query.isNegate() != result;
    }

    /**
     * Обрабатывает запрос USER_OF - проверяет использование продукта
     */
    private boolean evaluateUserOfQuery(UUID userId, List<String> arguments) {
        validateArguments(arguments, 1, "USER_OF");
        String productType = arguments.get(0);
        return repository.usesProductType(userId, productType);
    }

    /**
     * Обрабатывает запрос ACTIVE_USER_OF - проверяет активное использование
     */
    private boolean evaluateActiveUserOfQuery(UUID userId, List<String> arguments) {
        validateArguments(arguments, 1, "ACTIVE_USER_OF");
        String productType = arguments.get(0);
        return repository.isActiveUserOf(userId, productType);
    }

    /**
     * Обрабатывает запрос TRANSACTION_SUM_COMPARE - сравнение с константой
     */
    private boolean evaluateTransactionSumCompareQuery(UUID userId, List<String> arguments) {
        validateArguments(arguments, 4, "TRANSACTION_SUM_COMPARE");
        String productType = arguments.get(0);
        String transactionType = arguments.get(1);
        String operator = arguments.get(2);
        int constant = Integer.parseInt(arguments.get(3));

        return repository.compareTransactionSumWithConstant(userId, productType, transactionType, operator, constant);
    }

    /**
     * Обрабатывает запрос TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW - сравнение пополнений и трат
     */
    private boolean evaluateDepositWithdrawCompareQuery(UUID userId, List<String> arguments) {
        validateArguments(arguments, 2, "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW");
        String productType = arguments.get(0);
        String operator = arguments.get(1);

        return repository.compareDepositWithWithdraw(userId, productType, operator);
    }

    /**
     * Валидирует количество аргументов запроса
     */
    private void validateArguments(List<String> arguments, int expectedCount, String queryType) {
        if (arguments.size() != expectedCount) {
            throw new IllegalArgumentException(
                    String.format("Query %s requires %d arguments, but got %d",
                            queryType, expectedCount, arguments.size())
            );
        }
    }
}

