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
     * 创建并持久化一个默认启用的敏感数据规则（初始版本、当前时间作为创建/更新时间）。
     *
     * @param id              规则唯一标识
     * @param name            规则名称
     * @param type            规则类型（例如 KEYWORD/REGEX）
     * @param content         规则内容（关键词或正则表达式）
     * @param sensitiveDataType 敏感数据分类
     * @param riskLevel       风险等级
     */
    private void createDefaultRule(String id, String name, SensitiveDataRule.RuleType type, 
            String content, SensitiveDataType sensitiveDataType, com.sensitive.data.model.RiskLevel riskLevel) {
        LocalDateTime now = LocalDateTime.now();
        SensitiveDataRule rule = new SensitiveDataRule(
            id,
            name,
            type,
            content,
            sensitiveDataType,
            riskLevel,
            SensitiveDataRule.RuleStatus.ENABLED,
            now,
            now,
            1
        );
        
        ruleStore.put(id, rule);
    }
    
    /**
     * 创建并持久化一个敏感数据规则并将其同步到检测服务。
     *
     * <p>如果传入规则未提供 id，则为其生成唯一 id；为规则设置创建/更新时间为当前时间，版本号为 1，然后保存到规则存储并触发检测器规则刷新。</p>
     *
     * @param rule 待创建的规则对象；可以包含完整字段或不含 id（此时方法会生成 id）
     * @return 新创建并已保存的 {@code SensitiveDataRule}，其包含分配的 id、创建/更新时间和版本号 1
     */
    @Override
    public SensitiveDataRule createRule(SensitiveDataRule rule) {
        // 生成规则ID
        String id = StringUtils.hasText(rule.id()) ? rule.id() : generateRuleId(rule);
        
        // 设置默认值
        LocalDateTime now = LocalDateTime.now();
        
        // 创建新的规则实例
        SensitiveDataRule newRule = new SensitiveDataRule(
            id,
            rule.name(),
            rule.type(),
            rule.content(),
            rule.sensitiveDataType(),
            rule.riskLevel(),
            rule.status(),
            now,
            now,
            1
        );
        
        // 保存规则
        ruleStore.put(id, newRule);
        
        // 刷新检测服务规则
        refreshDetectorRules();
        
        return newRule;
    }
    
    /**
     * 使用提供的规则信息更新已存在的敏感数据规则并返回更新后的规则。
     *
     * @param rule 包含要更新字段的新规则对象；其 `id` 必须对应已存在的规则
     * @throws IllegalArgumentException 如果指定的规则 `id` 在存储中不存在
     * @return 更新后的 SensitiveDataRule 实例；保留原始 `createdAt`，将 `updatedAt` 设为当前时间，`version` 增加 1
     */
    @Override
    public SensitiveDataRule updateRule(SensitiveDataRule rule) {
        // 检查规则是否存在
        if (!ruleStore.containsKey(rule.id())) {
            throw new IllegalArgumentException("Rule not found: " + rule.id());
        }
        
        // 更新规则
        SensitiveDataRule existingRule = ruleStore.get(rule.id());
        LocalDateTime now = LocalDateTime.now();
        
        // 创建新的规则实例
        SensitiveDataRule updatedRule = new SensitiveDataRule(
            rule.id(),
            rule.name(),
            rule.type(),
            rule.content(),
            rule.sensitiveDataType(),
            rule.riskLevel(),
            rule.status(),
            existingRule.createdAt(),
            now,
            existingRule.version() + 1
        );
        
        // 保存规则
        ruleStore.put(rule.id(), updatedRule);
        
        // 刷新检测服务规则
        refreshDetectorRules();
        
        return updatedRule;
    }
    
    /**
     * 删除指定 ID 的敏感数据规则并刷新检测器使用的规则集合。
     *
     * @param id 要删除的规则 ID
     */
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
    
    /**
     * 按规则类型筛选并返回匹配的敏感数据规则。
     *
     * @param type 要筛选的规则类型（例如 KEYWORD、REGEX 等）
     * @return 包含所有类型等于给定 `type` 的规则的列表；没有匹配时返回空列表
     */
    @Override
    public List<SensitiveDataRule> getRulesByType(SensitiveDataRule.RuleType type) {
        return ruleStore.values().stream()
                .filter(rule -> rule.type() == type)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据敏感数据类型筛选并返回规则列表。
     *
     * @param sensitiveDataType 要筛选的敏感数据类型
     * @return 匹配指定敏感数据类型的规则列表；如果没有匹配项则返回空列表
     */
    @Override
    public List<SensitiveDataRule> getRulesBySensitiveDataType(SensitiveDataType sensitiveDataType) {
        return ruleStore.values().stream()
                .filter(rule -> rule.sensitiveDataType() == sensitiveDataType)
                .collect(Collectors.toList());
    }
    
    /**
     * 按规则状态筛选并返回所有匹配的敏感数据规则。
     *
     * @param status 要匹配的规则状态
     * @return 所有具有给定状态的敏感数据规则列表；不存在匹配项时返回空列表
     */
    @Override
    public List<SensitiveDataRule> getRulesByStatus(SensitiveDataRule.RuleStatus status) {
        return ruleStore.values().stream()
                .filter(rule -> rule.status() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * 将指定 ID 的规则设为启用状态并刷新检测器规则。
     *
     * @param id 要启用的规则的 ID
     * @return 被启用并持久化后的 {@link SensitiveDataRule}（状态为 ENABLED，updatedAt 已设置为当前时间）
     * @throws IllegalArgumentException 如果指定 ID 的规则不存在
     */
    @Override
    public SensitiveDataRule enableRule(String id) {
        // 检查规则是否存在
        SensitiveDataRule rule = ruleStore.get(id);
        if (rule == null) {
            throw new IllegalArgumentException("Rule not found: " + id);
        }
        
        // 启用规则
        LocalDateTime now = LocalDateTime.now();
        SensitiveDataRule updatedRule = new SensitiveDataRule(
            rule.id(),
            rule.name(),
            rule.type(),
            rule.content(),
            rule.sensitiveDataType(),
            rule.riskLevel(),
            SensitiveDataRule.RuleStatus.ENABLED,
            rule.createdAt(),
            now,
            rule.version()
        );
        
        // 保存规则
        ruleStore.put(id, updatedRule);
        
        // 刷新检测服务规则
        refreshDetectorRules();
        
        return updatedRule;
    }
    
    /**
     * 将指定 ID 的规则设置为禁用并返回更新后的规则。
     *
     * @param id 要禁用的规则的唯一标识
     * @return 被禁用并已更新其更新时间的规则对象
     * @throws IllegalArgumentException 当指定 ID 的规则不存在时抛出
     */
    @Override
    public SensitiveDataRule disableRule(String id) {
        // 检查规则是否存在
        SensitiveDataRule rule = ruleStore.get(id);
        if (rule == null) {
            throw new IllegalArgumentException("Rule not found: " + id);
        }
        
        // 禁用规则
        LocalDateTime now = LocalDateTime.now();
        SensitiveDataRule updatedRule = new SensitiveDataRule(
            rule.id(),
            rule.name(),
            rule.type(),
            rule.content(),
            rule.sensitiveDataType(),
            rule.riskLevel(),
            SensitiveDataRule.RuleStatus.DISABLED,
            rule.createdAt(),
            now,
            rule.version()
        );
        
        // 保存规则
        ruleStore.put(id, updatedRule);
        
        // 刷新检测服务规则
        refreshDetectorRules();
        
        return updatedRule;
    }
    
    /**
     * 批量更新或创建敏感数据规则，并在完成后刷新检测器使用的规则集合。
     *
     * @param rules 待处理的规则列表；对于已存在的规则（按 id 匹配）执行更新，否则创建新规则
     * @return 被创建或更新后的规则列表，顺序与输入列表对应
     */
    @Override
    public List<SensitiveDataRule> batchUpdateRules(List<SensitiveDataRule> rules) {
        List<SensitiveDataRule> updatedRules = new ArrayList<>();
        
        for (SensitiveDataRule rule : rules) {
            if (ruleStore.containsKey(rule.id())) {
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
    
    /**
     * 获取所有处于启用状态的关键词规则的内容列表。
     *
     * @return 启用且 type 为 KEYWORD 的规则的 content 字符串列表
     */
    @Override
    public List<String> getAllEnabledKeywords() {
        return ruleStore.values().stream()
                .filter(rule -> rule.type() == SensitiveDataRule.RuleType.KEYWORD)
                .filter(rule -> rule.status() == SensitiveDataRule.RuleStatus.ENABLED)
                .map(rule -> rule.content())
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有处于启用状态的正则规则对应的正则表达式模式列表。
     *
     * @return 包含每个已启用正则规则的 `content`（正则表达式字符串）的列表
     */
    @Override
    public List<String> getAllEnabledRegexPatterns() {
        return ruleStore.values().stream()
                .filter(rule -> rule.type() == SensitiveDataRule.RuleType.REGEX)
                .filter(rule -> rule.status() == SensitiveDataRule.RuleStatus.ENABLED)
                .map(rule -> rule.content())
                .collect(Collectors.toList());
    }
    
    /**
         * 基于规则的敏感数据类型、规则类型和当前时间生成唯一的规则 ID。
         *
         * @param rule 用于构建 ID 的规则，其 sensitiveDataType() 和 type() 会作为前缀
         * @return 形式为 `敏感数据类型_规则类型_时间戳` 的规则 ID 字符串（例如 `PERSONAL_KEYWORD_1617973123456`）
         */
    private String generateRuleId(SensitiveDataRule rule) {
        String prefix = rule.sensitiveDataType().name() + "_" + rule.type().name() + "_";
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