package com.Star_Bank.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Заглушка для Telegram бота (если зависимости нет)
 */
@Component
public class TelegramBotStub {
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotStub.class);

    public void start() {
        logger.info("Telegram bot stub started - bot functionality disabled");
        logger.info("To enable Telegram bot, add telegrambots dependency to pom.xml");
    }

    public String processCommand(String command) {
        if (command.startsWith("/recommend")) {
            return " Telegram bot disabled. Add telegrambots dependency to enable.";
        }
        return " Telegram bot not available";
    }
}
