package com.sensitive.data.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 敏感数据检测结果
 */
public record SensitiveDataDetectionResult(
    String id,
    String originalText,
    List<SensitiveDataItem> detectedItems,
    int totalDetected,
    long processingTimeMs,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
    /**
     * 构造函数，自动生成创建和更新时间
     */
    public SensitiveDataDetectionResult {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 构造函数，用于创建没有ID的检测结果
     */
    public SensitiveDataDetectionResult(String originalText, List<SensitiveDataItem> detectedItems, long processingTimeMs) {
        this(null, originalText, detectedItems, detectedItems != null ? detectedItems.size() : 0, processingTimeMs, LocalDateTime.now(), LocalDateTime.now());
    }
    
    /**
     * 构造函数，用于创建带有ID的检测结果
     */
    public SensitiveDataDetectionResult(String id, String originalText, List<SensitiveDataItem> detectedItems, long processingTimeMs) {
        this(id, originalText, detectedItems, detectedItems != null ? detectedItems.size() : 0, processingTimeMs, LocalDateTime.now(), LocalDateTime.now());
    }
    
    /**
     * 构造函数，用于创建空的检测结果
     */
    public SensitiveDataDetectionResult() {
        this(null, null, null, 0, 0, LocalDateTime.now(), LocalDateTime.now());
    }
}