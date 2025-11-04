package com.Star_Bank.model;
import jakarta.persistence.*;


import java.util.UUID;
/*Модель динамического правила рекомендаций для хранения в БД
 * Содержит информацию о продукте и набор запросов для проверки условий
 */
@Entity
@Table(name = "dynamic_rules")
public class DynamicRule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_text", columnDefinition = "TEXT")
    private String productText;

    /**
     * Сериализованный JSON с массивом запросов правила
     * Храним как текст для простоты, в production можно использовать JSONB
     */
    @Column(name = "rule_queries", columnDefinition = "TEXT")
    private String ruleQueries;

    // Конструкторы
    public DynamicRule() {}

    public DynamicRule(String productName, String productId, String productText, String ruleQueries) {
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.ruleQueries = ruleQueries;
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

    public String getRuleQueries() { return ruleQueries; }
    public void setRuleQueries(String ruleQueries) { this.ruleQueries = ruleQueries; }
}