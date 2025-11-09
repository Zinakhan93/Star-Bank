package com.Star_Bank.bot;

import com.Star_Bank.model.Recommendation;
import com.Star_Bank.model.RecommendationResponse;
import com.Star_Bank.servise.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Telegram бот для выдачи рекомендаций по имени пользователя
 */
@Component
public class RecommendationTelegramBot  extends TelegramLongPollingBot{
    private static final Logger logger = LoggerFactory.getLogger(RecommendationTelegramBot.class);

    private final String botToken;
    private final String botUsername;
    private final RecommendationService recommendationService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RecommendationTelegramBot(String botToken, String botUsername,
                                     RecommendationService recommendationService,
                                     JdbcTemplate jdbcTemplate) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.recommendationService = recommendationService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public RecommendationTelegramBot(String botToken, String botUsername) {
        // Конструктор для конфигурации без зависимостей
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.recommendationService = null;
        this.jdbcTemplate = null;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();
            Long chatId = message.getChatId();

            logger.info("Received message from {}: {}", chatId, text);

            if (text.startsWith("/recommend")) {
                handleRecommendCommand(chatId, text);
            } else if (text.equals("/start") || text.equals("/help")) {
                sendHelpMessage(chatId);
            } else {
                sendUnknownCommandMessage(chatId);
            }
        }
    }

    /**
     * Обрабатывает команду /recommend username
     */
    private void handleRecommendCommand(Long chatId, String text) {
        try {
            // Парсим команду: /recommend Ivan Ivanov
            String[] parts = text.split(" ", 3);
            if (parts.length < 2) {
                sendMessage(chatId, " Использование: /recommend <Имя Фамилия>");
                return;
            }

            String username = parts.length == 3 ? parts[1] + " " + parts[2] : parts[1];

            // Ищем пользователя по имени в базе данных
            Optional<String> userId = findUserIdByUsername(username);

            if (userId.isEmpty()) {
                sendMessage(chatId, " Пользователь не найден");
                return;
            }

            // Получаем рекомендации для найденного пользователя
            RecommendationResponse response = recommendationService.getRecommendations(UUID.fromString(userId.get()));

            // Форматируем ответ
            String responseText = formatRecommendationsResponse(username, response);
            sendMessage(chatId, responseText);

            logger.info("Sent recommendations for user {} to chat {}", username, chatId);

        } catch (Exception e) {
            logger.error("Error processing recommend command", e);
            sendMessage(chatId, " Произошла ошибка при обработке запроса");
        }
    }

    /**
     * Ищет ID пользователя по имени и фамилии
     */
    private Optional<String> findUserIdByUsername(String username) {
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
                logger.warn("Found multiple users with name: {}", username);
            }

            return Optional.empty();

        } catch (Exception e) {
            logger.error("Error finding user by username: {}", username, e);
            return Optional.empty();
        }
    }

    /**
     * Форматирует ответ с рекомендациями
     */
    private String formatRecommendationsResponse(String username, RecommendationResponse response) {
        StringBuilder sb = new StringBuilder();

        sb.append(" Здравствуйте, ").append(username).append("!\n\n");

        if (response.getRecommendations().isEmpty()) {
            sb.append(" На данный момент у нас нет персональных рекомендаций для вас.\n");
            sb.append("Возвращайтесь позже - мы постоянно обновляем наши предложения! 💫");
        } else {
            sb.append(" Новые продукты для вас:\n\n");

            for (int i = 0; i < response.getRecommendations().size(); i++) {
                Recommendation rec = response.getRecommendations().get(i);
                sb.append(i + 1).append(". **").append(rec.getName()).append("**\n");
                sb.append("   📝 ").append(rec.getText().split("\\.")[0]).append(".\n\n");
            }

            sb.append(" Хотите узнать подробнее? Обратитесь в отделение банка!");
        }

        return sb.toString();
    }

    /**
     * Отправляет справку по командам
     */
    private void sendHelpMessage(Long chatId) {
        String helpText = """
             *Бот рекомендаций банка*
            
            Доступные команды:
            
            /start, /help - показать эту справку
            /recommend <Имя Фамилия> - получить персональные рекомендации
            
            Пример:
            /recommend Иван Иванов
            """;

        sendMessage(chatId, helpText);
    }

    /**
     * Отправляет сообщение о неизвестной команде
     */
    private void sendUnknownCommandMessage(Long chatId) {
        sendMessage(chatId, " Неизвестная команда. Используйте /help для справки.");
    }

    /**
     * Отправляет сообщение в Telegram
     */
    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.enableMarkdown(true);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error sending message to chat {}", chatId, e);
        }
    }
}
