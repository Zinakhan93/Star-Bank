package com.Star_Bank.configuration;

import com.Star_Bank.bot.TelegramBotStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {
    @Value("${telegram.bot.token:}")
    private String botToken;

    @Value("${telegram.bot.username:}")
    private String botUsername;

    @Bean
    public TelegramBotStub telegramBotStub() {
        TelegramBotStub stub = new TelegramBotStub();
        stub.start();
        return stub;
    }

    // Бин для реального бота будет создаваться только если класс доступен
    @Bean
    public Object telegramBotsApi() {
        try {
            // Проверяем, доступна ли библиотека Telegram
            Class.forName("org.telegram.telegrambots.meta.TelegramBotsApi");

            // Если доступна, создаем реального бота
            return createRealBot();
        } catch (ClassNotFoundException e) {
            System.out.println("Telegram bots library not found - using stub");
            return new Object(); // Просто пустой объект
        }
    }

    private Object createRealBot() {
        System.out.println("Real Telegram bot would be created here");
        return new Object();
    }
}
