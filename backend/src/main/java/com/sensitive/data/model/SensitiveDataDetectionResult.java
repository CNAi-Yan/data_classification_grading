package com.sensitive.data.model;

import java.util.List;

/**
 * 敏感数据检测结果
 */
public class SensitiveDataDetectionResult {
    private String originalText;
    private List<SensitiveDataItem> detectedItems;
    private int totalDetected;
    private long processingTimeMs;
    
    public SensitiveDataDetectionResult() {
    }
    
    public SensitiveDataDetectionResult(String originalText, List<SensitiveDataItem> detectedItems, long processingTimeMs) {
        this.originalText = originalText;
        this.detectedItems = detectedItems;
        this.totalDetected = detectedItems != null ? detectedItems.size() : 0;
        this.processingTimeMs = processingTimeMs;
    }
    
    // Getters and Setters
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
    }
    
    public int getTotalDetected() {
        return totalDetected;
    }
    
    public long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
}