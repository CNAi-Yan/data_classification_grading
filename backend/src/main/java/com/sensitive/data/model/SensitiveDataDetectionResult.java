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
     * 创建一个不包含 id 的敏感数据检测结果实例。
     *
     * totalDetected 根据 detectedItems 的大小确定（detectedItems 为 null 时为 0）；createdAt 与 updatedAt 设置为当前时间。
     *
     * @param originalText    原始待检测文本
     * @param detectedItems   检测到的敏感数据项列表（可为 null）
     * @param processingTimeMs 检测耗时，单位为毫秒
     */
    public SensitiveDataDetectionResult(String originalText, List<SensitiveDataItem> detectedItems, long processingTimeMs) {
        this(null, originalText, detectedItems, detectedItems != null ? detectedItems.size() : 0, processingTimeMs, LocalDateTime.now(), LocalDateTime.now());
    }
    
    /**
     * 创建一个包含指定 ID 的敏感数据检测结果实例。
     *
     * 如果 {@code detectedItems} 为 {@code null}，则 {@code totalDetected} 设为 0；{@code createdAt} 和 {@code updatedAt} 使用当前时间。
     *
     * @param id              结果的唯一标识
     * @param originalText    原始待检测文本
     * @param detectedItems   检测到的敏感数据项列表（可为 {@code null}）
     * @param processingTimeMs 检测耗时，单位为毫秒
     */
    public SensitiveDataDetectionResult(String id, String originalText, List<SensitiveDataItem> detectedItems, long processingTimeMs) {
        this(id, originalText, detectedItems, detectedItems != null ? detectedItems.size() : 0, processingTimeMs, LocalDateTime.now(), LocalDateTime.now());
    }
    
    /**
     * 创建一个表示“空”检测结果的实例。
     *
     * 生成的记录字段为：id 和 originalText 为 null，detectedItems 为 null，totalDetected 和 processingTimeMs 为 0，
     * createdAt 和 updatedAt 使用当前时间初始化。
     */
    public SensitiveDataDetectionResult() {
        this(null, null, null, 0, 0, LocalDateTime.now(), LocalDateTime.now());
    }
}