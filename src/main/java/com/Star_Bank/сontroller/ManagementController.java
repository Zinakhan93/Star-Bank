package com.Star_Bank.сontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления сервисом и статистики
 */
@RestController
public class ManagementController {
    private final CacheManager cacheManager;
    private final StatisticService statisticService;
    private final BuildProperties buildProperties;

    @Autowired
    public ManagementController(CacheManager cacheManager,
                                StatisticService statisticService,
                                BuildProperties buildProperties) {
        this.cacheManager = cacheManager;
        this.statisticService = statisticService;
        this.buildProperties = buildProperties;
    }

    /**
     * Очищает все кеши приложения
     * POST /management/clear-caches
     */
    @PostMapping("/clear-caches")
    public ResponseEntity<String> clearCaches() {
        // Очищаем все кеши
        cacheManager.getCacheNames()
                .forEach(cacheName -> {
                    cacheManager.getCache(cacheName).clear();
                    System.out.println("Cleared cache: " + cacheName);
                });

        return ResponseEntity.ok("All caches cleared successfully");
    }

    /**
     * Возвращает информацию о сервисе
     * GET /management/info
     */
    @GetMapping("/info")
    public ResponseEntity<ServiceInfo> getServiceInfo() {
        ServiceInfo info = new ServiceInfo(
                buildProperties.getName(),
                buildProperties.getVersion()
        );
        return ResponseEntity.ok(info);
    }

    /**
     * Получает статистику срабатываний правил
     * GET /rule/stats
     */
    @GetMapping("/rule/stats")
    public ResponseEntity<RuleStatsResponse> getRuleStatistics() {
        RuleStatsResponse stats = statisticService.getStatistics();
        return ResponseEntity.ok(stats);
    }
}
