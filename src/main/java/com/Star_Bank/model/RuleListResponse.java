package com.Star_Bank.model;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO для ответа со списком правил
 */
public class RuleListResponse {
    private List<RuleResponse> data;

    public RuleListResponse() {
        this.data = new ArrayList<>();
    }

    public RuleListResponse(List<RuleResponse> data) {
        this.data = data;
    }

    public List<RuleResponse> getData() { return data; }
    public void setData(List<RuleResponse> data) { this.data = data; }
}

