package com.Star_Bank.model;

import java.util.List;

public class RuleQuery {
    /**
     * Тип запроса: USER_OF, ACTIVE_USER_OF, TRANSACTION_SUM_COMPARE, TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW
     */
    private String query;

    /**
     * Аргументы запроса - список строковых параметров
     */
    private List<String> arguments;

    /**
     * Флаг отрицания - если true, результат запроса инвертируется
     */
    private boolean negate;

    // Конструкторы
    public RuleQuery() {}

    public RuleQuery(String query, List<String> arguments, boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    // Геттеры и сеттеры
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public List<String> getArguments() { return arguments; }
    public void setArguments(List<String> arguments) { this.arguments = arguments; }

    public boolean isNegate() { return negate; }
    public void setNegate(boolean negate) { this.negate = negate; }
}
