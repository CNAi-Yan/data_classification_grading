package com.sensitive.data.model;

/**
 * 敏感数据项
 */
public class SensitiveDataItem {
    private String content;
    private SensitiveDataType type;
    private int startPosition;
    private int endPosition;
    private String suggestion;
    
    public SensitiveDataItem() {
    }
    
    public SensitiveDataItem(String content, SensitiveDataType type, int startPosition, int endPosition) {
        this.content = content;
        this.type = type;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }
    
    // Getters and Setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public SensitiveDataType getType() {
        return type;
    }
    
    public void setType(SensitiveDataType type) {
        this.type = type;
    }
    
    public int getStartPosition() {
        return startPosition;
    }
    
    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }
    
    public int getEndPosition() {
        return endPosition;
    }
    
    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }
    
    public String getSuggestion() {
        return suggestion;
    }
    
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}