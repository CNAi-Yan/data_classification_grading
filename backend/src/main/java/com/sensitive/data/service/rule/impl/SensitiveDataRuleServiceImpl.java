package com.sensitive.data.service.rule.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sensitive.data.model.SensitiveDataRule;
import com.sensitive.data.model.SensitiveDataType;
import com.sensitive.data.service.detector.impl.SensitiveDataDetectorServiceImpl;
import com.sensitive.data.service.rule.SensitiveDataRuleService;

/**
 * 敏感数据规则服务实现
 */
@Service
public class SensitiveDataRuleServiceImpl implements SensitiveDataRuleService {
    
    // 规则存储，使用ConcurrentHashMap保证线程安全
    private final Map<String, SensitiveDataRule> ruleStore = new ConcurrentHashMap<>();
    
    // 敏感数据检测服务
    private final SensitiveDataDetectorServiceImpl detectorService;
    
    /**
     * 构造函数
     * @param detectorService 敏感数据检测服务
     */
    @Autowired
    public SensitiveDataRuleServiceImpl(SensitiveDataDetectorServiceImpl detectorService) {
        this.detectorService = detectorService;
        // 初始化默认规则
        initializeDefaultRules();
    }
    
    /**
     * 初始化默认规则
     */
    private void initializeDefaultRules() {
        // 政治敏感词规则
        createDefaultRule("POLITICAL_KEYWORD_001", "台独", SensitiveDataRule.RuleType.KEYWORD, 
                "台独", SensitiveDataType.POLITICAL, com.sensitive.data.model.RiskLevel.HIGH);
        createDefaultRule("POLITICAL_KEYWORD_002", "法轮功", SensitiveDataRule.RuleType.KEYWORD, 
                "法轮功", SensitiveDataType.POLITICAL, com.sensitive.data.model.RiskLevel.HIGH);
        createDefaultRule("POLITICAL_KEYWORD_003", "颠覆政府", SensitiveDataRule.RuleType.KEYWORD, 
                "颠覆政府", SensitiveDataType.POLITICAL, com.sensitive.data.model.RiskLevel.HIGH);
        
        // 色情词汇规则
        createDefaultRule("PORNOGRAPHY_KEYWORD_001", "色情", SensitiveDataRule.RuleType.KEYWORD, 
                "色情", SensitiveDataType.PORNOGRAPHY, com.sensitive.data.model.RiskLevel.HIGH);
        createDefaultRule("PORNOGRAPHY_KEYWORD_002", "黄色", SensitiveDataRule.RuleType.KEYWORD, 
                "黄色", SensitiveDataType.PORNOGRAPHY, com.sensitive.data.model.RiskLevel.MEDIUM);
        createDefaultRule("PORNOGRAPHY_KEYWORD_003", "成人", SensitiveDataRule.RuleType.KEYWORD, 
                "成人", SensitiveDataType.PORNOGRAPHY, com.sensitive.data.model.RiskLevel.MEDIUM);
        
        // 暴力词汇规则
        createDefaultRule("VIOLENCE_KEYWORD_001", "暴力", SensitiveDataRule.RuleType.KEYWORD, 
                "暴力", SensitiveDataType.VIOLENCE, com.sensitive.data.model.RiskLevel.MEDIUM);
        createDefaultRule("VIOLENCE_KEYWORD_002", "杀人", SensitiveDataRule.RuleType.KEYWORD, 
                "杀人", SensitiveDataType.VIOLENCE, com.sensitive.data.model.RiskLevel.HIGH);
        createDefaultRule("VIOLENCE_KEYWORD_003", "血腥", SensitiveDataRule.RuleType.KEYWORD, 
                "血腥", SensitiveDataType.VIOLENCE, com.sensitive.data.model.RiskLevel.MEDIUM);
        
        // 版权词汇规则
        createDefaultRule("COPYRIGHT_KEYWORD_001", "盗版", SensitiveDataRule.RuleType.KEYWORD, 
                "盗版", SensitiveDataType.COPYRIGHT, com.sensitive.data.model.RiskLevel.MEDIUM);
        createDefaultRule("COPYRIGHT_KEYWORD_002", "侵权", SensitiveDataRule.RuleType.KEYWORD, 
                "侵权", SensitiveDataType.COPYRIGHT, com.sensitive.data.model.RiskLevel.MEDIUM);
        createDefaultRule("COPYRIGHT_KEYWORD_003", "破解版", SensitiveDataRule.RuleType.KEYWORD, 
                "破解版", SensitiveDataType.COPYRIGHT, com.sensitive.data.model.RiskLevel.MEDIUM);
    }
    
    /**
     * 创建默认规则
     */
    private void createDefaultRule(String id, String name, SensitiveDataRule.RuleType type, 
            String content, SensitiveDataType sensitiveDataType, com.sensitive.data.model.RiskLevel riskLevel) {
        SensitiveDataRule rule = new SensitiveDataRule();
        rule.setId(id);
        rule.setName(name);
        rule.setType(type);
        rule.setContent(content);
        rule.setSensitiveDataType(sensitiveDataType);
        rule.setRiskLevel(riskLevel);
        rule.setStatus(SensitiveDataRule.RuleStatus.ENABLED);
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        rule.setVersion(1);
        
        ruleStore.put(id, rule);
    }
    
    @Override
    public SensitiveDataRule createRule(SensitiveDataRule rule) {
        // 生成规则ID
        if (!StringUtils.hasText(rule.getId())) {
            rule.setId(generateRuleId(rule));
        }
        
        // 设置默认值
        LocalDateTime now = LocalDateTime.now();
        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);
        rule.setVersion(1);
        
        // 保存规则
        ruleStore.put(rule.getId(), rule);
        
        // 刷新检测服务规则
        refreshDetectorRules();
        
        return rule;
    }
    
    @Override
    public SensitiveDataRule updateRule(SensitiveDataRule rule) {
        // 检查规则是否存在
        if (!ruleStore.containsKey(rule.getId())) {
            throw new IllegalArgumentException("Rule not found: " + rule.getId());
        }
        
        // 更新规则
        SensitiveDataRule existingRule = ruleStore.get(rule.getId());
        rule.setCreatedAt(existingRule.getCreatedAt());
        rule.setUpdatedAt(LocalDateTime.now());
        rule.setVersion(existingRule.getVersion() + 1);
        
        // 保存规则
        ruleStore.put(rule.getId(), rule);
        
        // 刷新检测服务规则
        refreshDetectorRules();
        
        return rule;
    }
    
    @Override
    public void deleteRule(String id) {
        // 删除规则
        ruleStore.remove(id);
        
        // 刷新检测服务规则
        refreshDetectorRules();
    }
    
    @Override
    public Optional<SensitiveDataRule> getRule(String id) {
        return Optional.ofNullable(ruleStore.get(id));
    }
    
    @Override
    public List<SensitiveDataRule> getAllRules() {
        return new ArrayList<>(ruleStore.values());
    }
    
    @Override
    public List<SensitiveDataRule> getRulesByType(SensitiveDataRule.RuleType type) {
        return ruleStore.values().stream()
                .filter(rule -> rule.getType() == type)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SensitiveDataRule> getRulesBySensitiveDataType(SensitiveDataType sensitiveDataType) {
        return ruleStore.values().stream()
                .filter(rule -> rule.getSensitiveDataType() == sensitiveDataType)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SensitiveDataRule> getRulesByStatus(SensitiveDataRule.RuleStatus status) {
        return ruleStore.values().stream()
                .filter(rule -> rule.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    @Override
    public SensitiveDataRule enableRule(String id) {
        // 检查规则是否存在
        SensitiveDataRule rule = ruleStore.get(id);
        if (rule == null) {
            throw new IllegalArgumentException("Rule not found: " + id);
        }
        
        // 启用规则
        rule.setStatus(SensitiveDataRule.RuleStatus.ENABLED);
        rule.setUpdatedAt(LocalDateTime.now());
        
        // 保存规则
        ruleStore.put(id, rule);
        
        // 刷新检测服务规则
        refreshDetectorRules();
        
        return rule;
    }
    
    @Override
    public SensitiveDataRule disableRule(String id) {
        // 检查规则是否存在
        SensitiveDataRule rule = ruleStore.get(id);
        if (rule == null) {
            throw new IllegalArgumentException("Rule not found: " + id);
        }
        
        // 禁用规则
        rule.setStatus(SensitiveDataRule.RuleStatus.DISABLED);
        rule.setUpdatedAt(LocalDateTime.now());
        
        // 保存规则
        ruleStore.put(id, rule);
        
        // 刷新检测服务规则
        refreshDetectorRules();
        
        return rule;
    }
    
    @Override
    public List<SensitiveDataRule> batchUpdateRules(List<SensitiveDataRule> rules) {
        List<SensitiveDataRule> updatedRules = new ArrayList<>();
        
        for (SensitiveDataRule rule : rules) {
            if (ruleStore.containsKey(rule.getId())) {
                // 更新现有规则
                updatedRules.add(updateRule(rule));
            } else {
                // 创建新规则
                updatedRules.add(createRule(rule));
            }
        }
        
        // 刷新检测服务规则
        refreshDetectorRules();
        
        return updatedRules;
    }
    
    @Override
    public void refreshRules() {
        // 刷新检测服务规则
        refreshDetectorRules();
    }
    
    @Override
    public List<String> getAllEnabledKeywords() {
        return ruleStore.values().stream()
                .filter(rule -> rule.getType() == SensitiveDataRule.RuleType.KEYWORD)
                .filter(rule -> rule.getStatus() == SensitiveDataRule.RuleStatus.ENABLED)
                .map(SensitiveDataRule::getContent)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<String> getAllEnabledRegexPatterns() {
        return ruleStore.values().stream()
                .filter(rule -> rule.getType() == SensitiveDataRule.RuleType.REGEX)
                .filter(rule -> rule.getStatus() == SensitiveDataRule.RuleStatus.ENABLED)
                .map(SensitiveDataRule::getContent)
                .collect(Collectors.toList());
    }
    
    /**
     * 生成规则ID
     * @param rule 规则
     * @return 规则ID
     */
    private String generateRuleId(SensitiveDataRule rule) {
        String prefix = rule.getSensitiveDataType().name() + "_" + rule.getType().name() + "_";
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + timestamp;
    }
    
    /**
     * 刷新检测服务规则
     */
    private void refreshDetectorRules() {
        // 获取所有启用的关键词规则
        List<String> keywords = getAllEnabledKeywords();
        
        // 更新检测服务的关键词
        detectorService.updateSensitiveKeywords(keywords);
    }
}