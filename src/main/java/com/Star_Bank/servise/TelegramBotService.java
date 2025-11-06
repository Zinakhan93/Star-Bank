package com.Star_Bank.servise;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с Telegram ботом
 */
@Service
public class TelegramBotService {
    private final JdbcTemplate jdbcTemplate;

    public TelegramBotService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Ищет ID пользователя по имени и фамилии
     */
    public Optional<String> findUserIdByUsername(String username) {
        try {
            // Предполагаем, что в таблице users есть поля first_name и last_name
            String sql = "SELECT id FROM users WHERE CONCAT(first_name, ' ', last_name) = ? OR username = ?";

            List<String> userIds = jdbcTemplate.query(
                    sql,
                    new Object[]{username, username},
                    (rs, rowNum) -> rs.getString("id")
            );

            if (userIds.size() == 1) {
                return Optional.of(userIds.get(0));
            } else if (userIds.size() > 1) {
                System.out.println("Found multiple users with name: " + username);
            }

            return Optional.empty();

        } catch (Exception e) {
            System.out.println("Error finding user by username: " + username + " - " + e.getMessage());
            return Optional.empty();
        }
    }
}
