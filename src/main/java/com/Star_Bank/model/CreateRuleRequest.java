package com.Star_Bank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO для запроса на создание правила
 */
public class CreateRuleRequest {

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("product_text")
    private String productText;

    private List<RuleQuery> rule;

    // Геттеры и сеттеры
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductText() { return productText; }
    public void setProductText(String productText) { this.productText = productText; }

    public List<RuleQuery> getRule() { return rule; }
    public void setRule(List<RuleQuery> rule) { this.rule = rule; }
}


