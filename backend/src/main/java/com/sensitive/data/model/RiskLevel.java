package com.sensitive.data.model;

/**
 * 风险等级枚举
 */
public enum RiskLevel {
    HIGH("高风险", "#ff4d4f"),
    MEDIUM("中风险", "#faad14"),
    LOW("低风险", "#52c41a");
    
    private final String name;
    private final String color;
    
    RiskLevel(String name, String color) {
        this.name = name;
        this.color = color;
    }
    
    public String getName() {
        return name;
    }
    
    public String getColor() {
        return color;
    }
}