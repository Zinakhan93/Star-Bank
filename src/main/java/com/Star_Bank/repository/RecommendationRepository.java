package com.Star_Bank.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;
/**
 * Обновленный репозиторий с добавлением кеширования результатов запросов
 * Каждый метод кеширует результаты для одинаковых параметров
 */
@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Проверяет использование продукта с кешированием
     * Ключ кеша: userId + productType
     */
    @Cacheable(value = "userProductUsage", key = "{#userId, #productType}")
    public boolean usesProductType(UUID userId, String productType) {
        String sql = """
            SELECT COUNT(*) > 0 
            FROM transaction t 
            JOIN product p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = ?
            """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType));
    }

    /**
     * Проверяет активное использование продукта (>= 5 транзакций) с кешированием
     */
    @Cacheable(value = "userProductUsage", key = "{#userId, #productType, 'active'}")
    public boolean isActiveUserOf(UUID userId, String productType) {
        String sql = """
            SELECT COUNT(*) >= 5 
            FROM transaction t 
            JOIN product p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = ?
            """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType));
    }

    /**
     * Получает сумму транзакций с кешированием
     * Ключ кеша: userId + productType + transactionType
     */
    @Cacheable(value = "transactionSums", key = "{#userId, #productType, #transactionType}")
    public BigDecimal getTransactionSumByType(UUID userId, String productType, String transactionType) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0) 
            FROM transaction t 
            JOIN product p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = ? AND t.type = ?
            """;
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, productType, transactionType);
    }

    /**
     * Сравнивает сумму транзакций с константой с кешированием промежуточных результатов
     */
    public boolean compareTransactionSumWithConstant(UUID userId, String productType,
                                                     String transactionType, String operator, int constant) {
        BigDecimal sum = getTransactionSumByType(userId, productType, transactionType);
        return compareValues(sum, new BigDecimal(constant), operator);
    }

    /**
     * Сравнивает суммы депозитов и снятий с кешированием
     */
    @Cacheable(value = "depositWithdrawCompare", key = "{#userId, #productType}")
    public boolean compareDepositWithWithdraw(UUID userId, String productType, String operator) {
        BigDecimal depositSum = getTransactionSumByType(userId, productType, "DEPOSIT");
        BigDecimal withdrawSum = getTransactionSumByType(userId, productType, "WITHDRAW");
        return compareValues(depositSum, withdrawSum, operator);
    }

    /**
     * Вспомогательный метод для сравнения двух значений по оператору
     */
    private boolean compareValues(BigDecimal value1, BigDecimal value2, String operator) {
        return switch (operator) {
            case ">" -> value1.compareTo(value2) > 0;
            case "<" -> value1.compareTo(value2) < 0;
            case "=" -> value1.compareTo(value2) == 0;
            case ">=" -> value1.compareTo(value2) >= 0;
            case "<=" -> value1.compareTo(value2) <= 0;
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }
}
