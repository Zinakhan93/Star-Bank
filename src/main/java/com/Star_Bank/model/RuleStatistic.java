package com.Star_Bank.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;


import java.util.UUID;

/**
 * Сущность для хранения статистики срабатываний динамических правил
 */
@Entity
@Table(name = "rule_statistics")
public class RuleStatistic {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "rule_id", nullable = false, unique = true)
    private UUID ruleId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "count", nullable = false)
    private Long count = 0L;

    public RuleStatistic() {
    }

    public RuleStatistic(UUID ruleId, String productName) {
        this.ruleId = ruleId;
        this.productName = productName;
        this.count = 0L;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public void incrementCount() {
        this.count++;
    }
}
