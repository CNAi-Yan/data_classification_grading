package com.sensitive.data.model;

/**
 * 敏感数据项
 */
public record SensitiveDataItem(
    String content,
    SensitiveDataType type,
    int startPosition,
    int endPosition,
    String suggestion
) {
    
    /**
     * 构造函数，用于创建没有建议的敏感数据项
     */
    public SensitiveDataItem(String content, SensitiveDataType type, int startPosition, int endPosition) {
        this(content, type, startPosition, endPosition, null);
    }
    
    /**
     * 构造函数，使用默认建议
     */
    public SensitiveDataItem {
        if (suggestion == null) {
            suggestion = "建议对" + type.getName() + "进行脱敏处理";
        }
    }
}