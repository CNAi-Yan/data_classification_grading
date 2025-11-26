package com.sensitive.data.service.detector.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sensitive.data.model.SensitiveDataDetectionResult;
import com.sensitive.data.model.SensitiveDataItem;
import com.sensitive.data.model.SensitiveDataType;
import com.sensitive.data.service.detector.SensitiveDataDetectorService;
import com.sensitive.data.service.processor.SensitiveDataProcessorService;
import com.sensitive.data.util.AhoCorasick;
import com.sensitive.data.util.IdCardUtil;
import com.sensitive.data.util.LuhnUtil;
import com.sensitive.data.util.PerformanceMonitor;
import com.sensitive.data.util.regex.RegexPatterns;

import reactor.core.publisher.Mono;

/**
 * 敏感数据检测服务实现
 */
@Service
public class SensitiveDataDetectorServiceImpl implements SensitiveDataDetectorService {
    
    private final SensitiveDataProcessorService dataProcessorService;
    
    // 敏感关键词列表（示例）
    private volatile List<String> sensitiveKeywords = new ArrayList<>();
    
    // Aho-Corasick算法实例，用于高效的多模式关键词匹配
    private volatile AhoCorasick ahoCorasick;
    
    // 性能监控器，用于记录和统计检测性能指标
    private static final PerformanceMonitor PERFORMANCE_MONITOR = new PerformanceMonitor();
    
    // 规则刷新间隔（秒）
    @Value("${sensitive.data.detector.rules.refresh-interval:300}")
    private long rulesRefreshInterval;
    
    // 最大规则数量
    @Value("${sensitive.data.detector.rules.max-rules:10000}")
    private int maxRules;
    
    // 缓存启用标志
    @Value("${sensitive.data.detector.cache.enabled:true}")
    private boolean cacheEnabled;
    
    /**
     * 构造函数
     * @param dataProcessorService 敏感数据处理服务
     */
    @Autowired
    public SensitiveDataDetectorServiceImpl(SensitiveDataProcessorService dataProcessorService) {
        this.dataProcessorService = dataProcessorService;
        // 初始化敏感关键词
        initializeSensitiveKeywords();
        // 初始化Aho-Corasick算法
        this.ahoCorasick = new AhoCorasick(sensitiveKeywords);
    }
    
    /**
     * 初始化敏感关键词
     */
    private void initializeSensitiveKeywords() {
        // 政治敏感词
        sensitiveKeywords.add("台独");
        sensitiveKeywords.add("法轮功");
        sensitiveKeywords.add("颠覆政府");
        
        // 色情词汇
        sensitiveKeywords.add("色情");
        sensitiveKeywords.add("黄色");
        sensitiveKeywords.add("成人");
        
        // 暴力词汇
        sensitiveKeywords.add("暴力");
        sensitiveKeywords.add("杀人");
        sensitiveKeywords.add("血腥");
        
        // 版权词汇
        sensitiveKeywords.add("盗版");
        sensitiveKeywords.add("侵权");
        sensitiveKeywords.add("破解版");
    }
    
    /**
     * 检测敏感数据（同步方法）
     * @param text 待检测文本
     * @return 检测结果
     */
    @Override
    @Cacheable(value = "sensitiveDataDetection", key = "#text", unless = "#result == null")
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
        long processingTime = endTime - startTime;
        
        // 记录性能指标
        PERFORMANCE_MONITOR.recordRequest(processingTime, detectedItems.size(), true);
        
        return new SensitiveDataDetectionResult(text, detectedItems, processingTime);
    }
    
    /**
     * 异步检测敏感数据
     * @param text 待检测文本
     * @return 异步检测结果
     */
    @Override
    @Async("detectorThreadPool")
    public Mono<SensitiveDataDetectionResult> detectSensitiveDataAsync(String text) {
        return Mono.just(detectSensitiveData(text));
    }
    
    /**
     * 批量检测敏感数据
     * @param texts 待检测文本列表
     * @return 检测结果列表
     */
    @Override
    @Async("detectorThreadPool")
    public Mono<List<SensitiveDataDetectionResult>> detectSensitiveDataBatch(List<String> texts) {
        List<SensitiveDataDetectionResult> results = texts.stream()
                .map(this::detectSensitiveData)
                .collect(Collectors.toList());
        return Mono.just(results);
    }
    
    /**
     * 实时检测敏感数据
     * @param text 待检测文本
     * @return 检测结果
     */
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
        long processingTime = endTime - startTime;
        
        // 记录性能指标
        PERFORMANCE_MONITOR.recordRequest(processingTime, detectedItems.size(), true);
        
        return new SensitiveDataDetectionResult(text, detectedItems, processingTime);
    }
    
    /**
     * 检测结构化敏感数据
     */
    private void detectStructuredData(String text, List<SensitiveDataItem> detectedItems) {
        // 使用顺序流处理所有敏感数据类型
        List<SensitiveDataItem> items = java.util.stream.Stream.of(SensitiveDataType.values())
                .sequential() // 启用顺序流
                .flatMap(type -> {
                    Pattern pattern = RegexPatterns.getPattern(type);
                    if (pattern == null) {
                        // 如果没有对应的正则表达式模式，跳过该类型
                        return java.util.stream.Stream.empty();
                    }
                    Matcher matcher = pattern.matcher(text);
                    List<SensitiveDataItem> typeItems = new ArrayList<>();
                    
                    while (matcher.find()) {
                        String content;
                        int startPos;
                        int endPos;
                        
                        // 对于身份证号类型，使用捕获组提取身份证号内容
                        if (type == SensitiveDataType.ID_CARD) {
                            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                                content = matcher.group(1);
                                startPos = matcher.start(1);
                                endPos = matcher.end(1);
                            } else {
                                continue;
                            }
                        } 
                        // 对于银行账号类型，使用捕获组提取银行账号内容
                        else if (type == SensitiveDataType.BANK_ACCOUNT) {
                            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                                content = matcher.group(1);
                                startPos = matcher.start(1);
                                endPos = matcher.end(1);
                            } else {
                                continue;
                            }
                        } 
                        // 对于密码类型，使用捕获组提取密码内容
                        else if (type == SensitiveDataType.PASSWORD) {
                            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                                content = matcher.group(1);
                                startPos = matcher.start(1);
                                endPos = matcher.end(1);
                            } else {
                                continue;
                            }
                        } else {
                            content = matcher.group();
                            startPos = matcher.start();
                            endPos = matcher.end();
                        }
                        
                        // 对于银行卡号和信用卡号，添加Luhn算法校验
                        if (type == SensitiveDataType.BANK_CARD || type == SensitiveDataType.CREDIT_CARD) {
                            if (LuhnUtil.isValidCardNumber(content)) {
                                typeItems.add(new SensitiveDataItem(content, type, startPos, endPos));
                            }
                        } 
                        // 对于身份证号，添加校验码验证
                        else if (type == SensitiveDataType.ID_CARD) {
                            if (IdCardUtil.isValidIdCard(content)) {
                                typeItems.add(new SensitiveDataItem(content, type, startPos, endPos));
                            }
                        } else {
                            typeItems.add(new SensitiveDataItem(content, type, startPos, endPos));
                        }
                    }
                    
                    return typeItems.stream();
                })
                .collect(Collectors.toList());
        
        detectedItems.addAll(items);
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
        
        // 使用顺序流处理高风险类型
        List<SensitiveDataItem> items = java.util.stream.Stream.of(highRiskTypes)
                .sequential() // 启用顺序流
                .filter(RegexPatterns::isSupported)
                .flatMap(type -> {
                    Pattern pattern = RegexPatterns.getPattern(type);
                    Matcher matcher = pattern.matcher(text);
                    List<SensitiveDataItem> typeItems = new ArrayList<>();
                    
                    while (matcher.find()) {
                        String content;
                        int startPos;
                        int endPos;
                        
                        // 对于密码类型，使用捕获组提取密码内容
                        if (type == SensitiveDataType.PASSWORD) {
                            if (matcher.groupCount() > 0 && matcher.group(1) != null) {
                                content = matcher.group(1);
                                startPos = matcher.start(1);
                                endPos = matcher.end(1);
                            } else {
                                continue;
                            }
                        } else {
                            content = matcher.group();
                            startPos = matcher.start();
                            endPos = matcher.end();
                        }
                        
                        // 对于银行卡号和信用卡号，添加Luhn算法校验
                        if (type == SensitiveDataType.BANK_CARD || type == SensitiveDataType.CREDIT_CARD) {
                            if (LuhnUtil.isValidCardNumber(content)) {
                                typeItems.add(new SensitiveDataItem(content, type, startPos, endPos));
                            }
                        } else {
                            typeItems.add(new SensitiveDataItem(content, type, startPos, endPos));
                        }
                    }
                    
                    return typeItems.stream();
                })
                .collect(Collectors.toList());
        
        detectedItems.addAll(items);
    }
    
    /**
     * 检测非结构化敏感数据（使用Aho-Corasick算法优化）
     */
    private void detectUnstructuredData(String text, List<SensitiveDataItem> detectedItems) {
        // 使用Aho-Corasick算法进行高效的多模式匹配
        List<AhoCorasick.MatchResult> matchResults = ahoCorasick.match(text);
        
        // 将匹配结果转换为SensitiveDataItem
        for (AhoCorasick.MatchResult result : matchResults) {
            String keyword = result.getPattern();
            SensitiveDataType type = determineKeywordType(keyword);
            SensitiveDataItem item = new SensitiveDataItem(keyword, type, result.getStart(), result.getEnd());
            detectedItems.add(item);
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
    
    /**
     * 更新敏感关键词列表
     * @param newKeywords 新的敏感关键词列表
     */
    public void updateSensitiveKeywords(List<String> newKeywords) {
        if (newKeywords != null && !newKeywords.isEmpty()) {
            // 限制关键词数量，防止内存溢出
            if (newKeywords.size() > maxRules) {
                this.sensitiveKeywords = newKeywords.subList(0, maxRules);
            } else {
                this.sensitiveKeywords = newKeywords;
            }
            // 重新初始化Aho-Corasick算法
            this.ahoCorasick = new AhoCorasick(this.sensitiveKeywords);
        }
    }
    
    /**
     * 获取当前敏感关键词列表
     * @return 敏感关键词列表
     */
    public List<String> getSensitiveKeywords() {
        return new ArrayList<>(sensitiveKeywords);
    }
}