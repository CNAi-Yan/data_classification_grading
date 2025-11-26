package com.sensitive.data.service.detector.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.sensitive.data.model.SensitiveDataDetectionResult;
import com.sensitive.data.model.SensitiveDataItem;
import com.sensitive.data.model.SensitiveDataType;
import com.sensitive.data.service.detector.SensitiveDataDetectorService;
import com.sensitive.data.util.regex.RegexPatterns;
import com.sensitive.data.service.processor.SensitiveDataProcessorService;

/**
 * 敏感数据检测服务实现
 */
@Service
public class SensitiveDataDetectorServiceImpl implements SensitiveDataDetectorService {
    
    private final SensitiveDataProcessorService dataProcessorService;
    
    // 敏感关键词列表（示例）
    private static final List<String> SENSITIVE_KEYWORDS = new ArrayList<>();
    
    static {
        // 政治敏感词
        SENSITIVE_KEYWORDS.add("台独");
        SENSITIVE_KEYWORDS.add("法轮功");
        SENSITIVE_KEYWORDS.add("颠覆政府");
        
        // 色情词汇
        SENSITIVE_KEYWORDS.add("色情");
        SENSITIVE_KEYWORDS.add("黄色");
        SENSITIVE_KEYWORDS.add("成人");
        
        // 暴力词汇
        SENSITIVE_KEYWORDS.add("暴力");
        SENSITIVE_KEYWORDS.add("杀人");
        SENSITIVE_KEYWORDS.add("血腥");
        
        // 版权词汇
        SENSITIVE_KEYWORDS.add("盗版");
        SENSITIVE_KEYWORDS.add("侵权");
        SENSITIVE_KEYWORDS.add("破解版");
    }
    
    public SensitiveDataDetectorServiceImpl(SensitiveDataProcessorService dataProcessorService) {
        this.dataProcessorService = dataProcessorService;
    }
    
    @Override
    public SensitiveDataDetectionResult detectSensitiveData(String text) {
        if (StringUtils.isBlank(text)) {
            return new SensitiveDataDetectionResult("", new ArrayList<>(), 0);
        }
        
        long startTime = System.currentTimeMillis();
        List<SensitiveDataItem> detectedItems = new ArrayList<>();
        
        // 1. 使用正则表达式检测结构化敏感数据
        detectStructuredData(text, detectedItems);
        
        // 2. 使用关键词匹配检测非结构化敏感数据
        detectUnstructuredData(text, detectedItems);
        
        // 3. 为每个检测到的项目添加处理建议
        for (SensitiveDataItem item : detectedItems) {
            item.setSuggestion(dataProcessorService.getProcessingSuggestion(item.getType()));
        }
        
        long endTime = System.currentTimeMillis();
        
        return new SensitiveDataDetectionResult(text, detectedItems, endTime - startTime);
    }
    
    @Override
    public SensitiveDataDetectionResult detectSensitiveDataRealtime(String text) {
        // 实时检测可以使用更简化的规则，提高响应速度
        if (StringUtils.isBlank(text)) {
            return new SensitiveDataDetectionResult("", new ArrayList<>(), 0);
        }
        
        long startTime = System.currentTimeMillis();
        List<SensitiveDataItem> detectedItems = new ArrayList<>();
        
        // 只检测高风险的结构化数据
        detectHighRiskStructuredData(text, detectedItems);
        
        // 为每个检测到的项目添加处理建议
        for (SensitiveDataItem item : detectedItems) {
            item.setSuggestion(dataProcessorService.getProcessingSuggestion(item.getType()));
        }
        
        long endTime = System.currentTimeMillis();
        
        return new SensitiveDataDetectionResult(text, detectedItems, endTime - startTime);
    }
    
    /**
     * 检测结构化敏感数据（使用正则表达式）
     */
    private void detectStructuredData(String text, List<SensitiveDataItem> detectedItems) {
        for (SensitiveDataType type : SensitiveDataType.values()) {
            if (RegexPatterns.isSupported(type)) {
                Pattern pattern = RegexPatterns.getPattern(type);
                Matcher matcher = pattern.matcher(text);
                
                while (matcher.find()) {
                    String content = matcher.group();
                    int startPos = matcher.start();
                    int endPos = matcher.end();
                    
                    SensitiveDataItem item = new SensitiveDataItem(content, type, startPos, endPos);
                    detectedItems.add(item);
                }
            }
        }
    }
    
    /**
     * 检测高风险结构化数据（用于实时检测）
     */
    private void detectHighRiskStructuredData(String text, List<SensitiveDataItem> detectedItems) {
        // 只检测高风险类型
        SensitiveDataType[] highRiskTypes = {
            SensitiveDataType.ID_CARD,
            SensitiveDataType.BANK_CARD,
            SensitiveDataType.CREDIT_CARD,
            SensitiveDataType.PASSWORD
        };
        
        for (SensitiveDataType type : highRiskTypes) {
            if (RegexPatterns.isSupported(type)) {
                Pattern pattern = RegexPatterns.getPattern(type);
                Matcher matcher = pattern.matcher(text);
                
                while (matcher.find()) {
                    String content = matcher.group();
                    int startPos = matcher.start();
                    int endPos = matcher.end();
                    
                    SensitiveDataItem item = new SensitiveDataItem(content, type, startPos, endPos);
                    detectedItems.add(item);
                }
            }
        }
    }
    
    /**
     * 检测非结构化敏感数据（使用关键词匹配）
     */
    private void detectUnstructuredData(String text, List<SensitiveDataItem> detectedItems) {
        for (String keyword : SENSITIVE_KEYWORDS) {
            int index = 0;
            while ((index = text.indexOf(keyword, index)) != -1) {
                SensitiveDataType type = determineKeywordType(keyword);
                SensitiveDataItem item = new SensitiveDataItem(keyword, type, index, index + keyword.length());
                detectedItems.add(item);
                index += keyword.length();
            }
        }
    }
    
    /**
     * 根据关键词确定敏感数据类型
     */
    private SensitiveDataType determineKeywordType(String keyword) {
        // 政治敏感词
        if (keyword.contains("台独") || keyword.contains("法轮功") || keyword.contains("颠覆政府")) {
            return SensitiveDataType.POLITICAL;
        }
        
        // 色情词汇
        if (keyword.contains("色情") || keyword.contains("黄色") || keyword.contains("成人")) {
            return SensitiveDataType.PORNOGRAPHY;
        }
        
        // 暴力词汇
        if (keyword.contains("暴力") || keyword.contains("杀人") || keyword.contains("血腥")) {
            return SensitiveDataType.VIOLENCE;
        }
        
        // 版权词汇
        if (keyword.contains("盗版") || keyword.contains("侵权") || keyword.contains("破解版")) {
            return SensitiveDataType.COPYRIGHT;
        }
        
        // 默认返回政治敏感
        return SensitiveDataType.POLITICAL;
    }
}