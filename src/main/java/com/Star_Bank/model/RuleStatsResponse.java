package com.Star_Bank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO для ответа статистики срабатываний правил
 */
public class RuleStatsResponse {

    public static class RuleStat {
        @JsonProperty("rule_id")
        private UUID ruleId;

        private Long count;

        public RuleStat() {}

        public RuleStat(UUID ruleId, Long count) {
            this.ruleId = ruleId;
            this.count = count;
        }

        public UUID getRuleId() { return ruleId; }
        public void setRuleId(UUID ruleId) { this.ruleId = ruleId; }

        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }

    private List<RuleStat> stats;

    public RuleStatsResponse() {
        this.stats = new ArrayList<>();
    }

    public RuleStatsResponse(List<RuleStat> stats) {
        this.stats = stats;
    }

    public List<RuleStat> getStats() { return stats; }
    public void setStats(List<RuleStat> stats) { this.stats = stats; }
}
