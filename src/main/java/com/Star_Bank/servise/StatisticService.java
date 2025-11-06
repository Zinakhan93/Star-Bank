package com.Star_Bank.servise;

import com.Star_Bank.model.RuleStatistic;
import com.Star_Bank.model.RuleStatsResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для управления статистикой срабатываний динамических правил
 */
public class StatisticService {
    private static final Logger logger = LoggerFactory.getLogger(StatisticService.class);

    private final RuleStatisticRepository statisticRepository;

    @Autowired
    public StatisticService(RuleStatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
    }

    @Transactional
    public void recordRuleHit(UUID ruleId, String productName) {
        // Ищем существующую статистику или создаем новую
        RuleStatistic statistic = statisticRepository.findByRuleId(ruleId)
                .orElseGet(() -> {
                    RuleStatistic newStatistic = new RuleStatistic(ruleId, productName);
                    return statisticRepository.save(newStatistic);
                });

        // Увеличиваем счетчик
        statisticRepository.incrementCount(ruleId);
        logger.debug("Recorded hit for rule {} ({}), total hits: {}",
                ruleId, productName, statistic.getCount() + 1);
    }

    public RuleStatsResponse getStatistics() {
        List<RuleStatistic> allStatistics = statisticRepository.findAll();

        // Преобразуем в DTO
        List<RuleStatsResponse.RuleStat> stats = allStatistics.stream()
                .map(stat -> new RuleStatsResponse.RuleStat(stat.getRuleId(), stat.getCount()))
                .collect(Collectors.toList());

        logger.info("Retrieved statistics for {} rules", stats.size());
        return new RuleStatsResponse(stats);
    }

    @Transactional
    public void deleteStatisticsForRule(UUID ruleId) {
        statisticRepository.deleteByRuleId(ruleId);
        logger.info("Deleted statistics for rule: {}", ruleId);
    }

    @Transactional
    public void initializeStatisticsForRule(UUID ruleId, String productName) {
        if (!statisticRepository.findByRuleId(ruleId).isPresent()) {
            RuleStatistic statistic = new RuleStatistic(ruleId, productName);
            statisticRepository.save(statistic);
            logger.info("Initialized statistics for new rule: {} ({})", ruleId, productName);
        }
    }
}
