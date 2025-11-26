package com.sensitive.data.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 敏感数据检测结果
 */
public class SensitiveDataDetectionResult {
    private String id;
    private String originalText;
    private List<SensitiveDataItem> detectedItems;
    private int totalDetected;
    private long processingTimeMs;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public SensitiveDataDetectionResult() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public SensitiveDataDetectionResult(String originalText, List<SensitiveDataItem> detectedItems, long processingTimeMs) {
        this.originalText = originalText;
        this.detectedItems = detectedItems;
        this.totalDetected = detectedItems != null ? detectedItems.size() : 0;
        this.processingTimeMs = processingTimeMs;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public SensitiveDataDetectionResult(String id, String originalText, List<SensitiveDataItem> detectedItems, long processingTimeMs) {
        this.id = id;
        this.originalText = originalText;
        this.detectedItems = detectedItems;
        this.totalDetected = detectedItems != null ? detectedItems.size() : 0;
        this.processingTimeMs = processingTimeMs;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getOriginalText() {
        return originalText;
    }
    
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }
    
    public List<SensitiveDataItem> getDetectedItems() {
        return detectedItems;
    }
    
    public void setDetectedItems(List<SensitiveDataItem> detectedItems) {
        this.detectedItems = detectedItems;
        this.totalDetected = detectedItems != null ? detectedItems.size() : 0;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getTotalDetected() {
        return totalDetected;
    }
    
    public long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
        this.updatedAt = LocalDateTime.now();
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
}