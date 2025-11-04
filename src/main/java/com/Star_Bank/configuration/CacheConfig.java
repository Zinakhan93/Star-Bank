package com.Star_Bank.configuration;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * Конфигурация кеширования с использованием Caffeine
 * Кеширует результаты запросов к основной БД для повышения производительности
 */
public class CacheConfig {

    /**
     * Создает менеджер кешей с настройками TTL и размера
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "userProductUsage",      // Кеш для проверки использования продуктов
                "transactionSums",       // Кеш для сумм транзакций
                "depositWithdrawCompare" // Кеш для сравнения пополнений и трат
        );

        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * Настройки кеша:
     * - Максимальный размер: 1000 записей
     * - TTL: 1 час (так как данные пользователей не меняются)
     */
    private CacheProperties.Caffeine<Object, Object> caffeineCacheBuilder() {
        return CacheProperties.Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats();
    }
}
}
