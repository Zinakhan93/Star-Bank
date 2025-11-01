package com.Star_Bank.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserDataRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserDataRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getRandomTransactionAmount(UUID user){
        Integer result = jdbcTemplate.queryForObject(
                "SELECT amount FROM transactions t WHERE t.user_id = ? LIMIT 1",
                Integer.class,
                user);
        return result != null ? result : 0;
    }
    // Проверяет, использует ли пользователь продукт определенного типа
    public boolean usesProductType(UUID userId, String productType) {
        String sql = """
            SELECT COUNT(*) > 0 
            FROM transactions t 
            JOIN products p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = ?
            """;

        Boolean result = jdbcTemplate.queryForObject(sql, Boolean.class, userId, productType);
        return Boolean.TRUE.equals(result);
    }
    // Получает сумму пополнений по типу продукта
    public Long getTotalDepositsByProductType(UUID userId, String productType) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0) 
            FROM transactions t 
            JOIN products p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = ? AND t.type = 'DEPOSIT'
            """;

        Long result = jdbcTemplate.queryForObject(sql, Long.class, userId, productType);
        return result != null ? result : 0L;
    }

    // Получает сумму трат по типу продукта
    public Long getTotalSpendsByProductType(UUID userId, String productType) {
        String sql = """
            SELECT COALESCE(SUM(t.amount), 0) 
            FROM transactions t 
            JOIN products p ON t.product_id = p.id 
            WHERE t.user_id = ? AND p.type = ? AND t.type = 'WITHDRAW'
            """;

        Long result = jdbcTemplate.queryForObject(sql, Long.class, userId, productType);
        return result != null ? result : 0L;
    }

    // Получает сумму пополнений по конкретному продукту
    public Long getTotalDepositsByProduct(UUID userId, UUID productId) {
        String sql = """
            SELECT COALESCE(SUM(amount), 0) 
            FROM transactions 
            WHERE user_id = ? AND product_id = ? AND type = 'DEPOSIT'
            """;

        Long result = jdbcTemplate.queryForObject(sql, Long.class, userId, productId);
        return result != null ? result : 0L;
    }
}
