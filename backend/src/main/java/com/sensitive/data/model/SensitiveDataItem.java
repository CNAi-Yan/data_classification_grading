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
     * 创建一个不带建议的敏感数据项实例。
     *
     * @param content 敏感数据的原文
     * @param type 敏感数据的类型
     * @param startPosition 在原始文本中的起始索引
     * @param endPosition 在原始文本中的结束索引
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