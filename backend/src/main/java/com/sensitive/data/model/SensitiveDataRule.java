package com.sensitive.data.model;

import java.time.LocalDateTime;

/**
 * 敏感数据规则模型
 */
public record SensitiveDataRule(
    String id,
    String name,
    RuleType type,
    String content,
    SensitiveDataType sensitiveDataType,
    RiskLevel riskLevel,
    RuleStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int version
) {
    
    /**
     * 构造函数，自动生成创建和更新时间
     */
    public SensitiveDataRule {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
    
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
}