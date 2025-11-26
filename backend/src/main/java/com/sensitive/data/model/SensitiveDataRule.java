package com.sensitive.data.model;

import java.time.LocalDateTime;

/**
 * 敏感数据规则模型
 */
public class SensitiveDataRule {
    
    /**
     * 规则ID
     */
    private String id;
    
    /**
     * 规则名称
     */
    private String name;
    
    /**
     * 规则类型
     */
    private RuleType type;
    
    /**
     * 规则内容
     */
    private String content;
    
    /**
     * 敏感数据类型
     */
    private SensitiveDataType sensitiveDataType;
    
    /**
     * 风险等级
     */
    private RiskLevel riskLevel;
    
    /**
     * 规则状态
     */
    private RuleStatus status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 规则版本
     */
    private int version;
    
    /**
     * 规则类型枚举
     */
    public enum RuleType {
        /**
         * 正则表达式规则
         */
        REGEX,
        
        /**
         * 关键词规则
         */
        KEYWORD,
        
        /**
         * 复合规则
         */
        COMPOSITE
    }
    
    /**
     * 规则状态枚举
     */
    public enum RuleStatus {
        /**
         * 启用
         */
        ENABLED,
        
        /**
         * 禁用
         */
        DISABLED,
        
        /**
         * 待审核
         */
        PENDING
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public RuleType getType() {
        return type;
    }
    
    public void setType(RuleType type) {
        this.type = type;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public SensitiveDataType getSensitiveDataType() {
        return sensitiveDataType;
    }
    
    public void setSensitiveDataType(SensitiveDataType sensitiveDataType) {
        this.sensitiveDataType = sensitiveDataType;
    }
    
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public RuleStatus getStatus() {
        return status;
    }
    
    public void setStatus(RuleStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    @Override
    public String toString() {
        return "SensitiveDataRule{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", sensitiveDataType=" + sensitiveDataType +
                ", riskLevel=" + riskLevel +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }
}