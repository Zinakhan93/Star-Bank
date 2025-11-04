package com.Star_Bank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

/**
 * DTO для ответов API динамических правил
 */
public class RuleResponse {
    private UUID id;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("product_text")
    private String productText;

    private List<RuleQuery> rule;

    // Конструкторы
    public RuleResponse() {}

    public RuleResponse(UUID id, String productName, String productId, String productText, List<RuleQuery> rule) {
        this.id = id;
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.rule = rule;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductText() { return productText; }
    public void setProductText(String productText) { this.productText = productText; }

    public List<RuleQuery> getRule() { return rule; }
    public void setRule(List<RuleQuery> rule) { this.rule = rule; }
}

