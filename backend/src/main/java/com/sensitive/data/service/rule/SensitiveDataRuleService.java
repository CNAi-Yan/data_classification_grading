package com.sensitive.data.service.rule;

import java.util.List;
import java.util.Optional;

import com.sensitive.data.model.SensitiveDataRule;
import com.sensitive.data.model.SensitiveDataType;

/**
 * 敏感数据规则服务接口
 */
public interface SensitiveDataRuleService {
    
    /**
     * 创建敏感数据规则
     * 
     * @param rule 敏感数据规则
     * @return 创建的规则
     */
    SensitiveDataRule createRule(SensitiveDataRule rule);
    
    /**
     * 更新敏感数据规则
     * 
     * @param rule 敏感数据规则
     * @return 更新后的规则
     */
    SensitiveDataRule updateRule(SensitiveDataRule rule);
    
    /**
     * 删除敏感数据规则
     * 
     * @param id 规则ID
     */
    void deleteRule(String id);
    
    /**
     * 获取敏感数据规则
     * 
     * @param id 规则ID
     * @return 敏感数据规则
     */
    Optional<SensitiveDataRule> getRule(String id);
    
    /**
     * 获取所有敏感数据规则
     * 
     * @return 敏感数据规则列表
     */
    List<SensitiveDataRule> getAllRules();
    
    /**
     * 根据类型获取敏感数据规则
     * 
     * @param type 规则类型
     * @return 敏感数据规则列表
     */
    List<SensitiveDataRule> getRulesByType(SensitiveDataRule.RuleType type);
    
    /**
     * 根据敏感数据类型获取规则
     * 
     * @param sensitiveDataType 敏感数据类型
     * @return 敏感数据规则列表
     */
    List<SensitiveDataRule> getRulesBySensitiveDataType(SensitiveDataType sensitiveDataType);
    
    /**
     * 根据状态获取规则
     * 
     * @param status 规则状态
     * @return 敏感数据规则列表
     */
    List<SensitiveDataRule> getRulesByStatus(SensitiveDataRule.RuleStatus status);
    
    /**
     * 启用规则
     * 
     * @param id 规则ID
     * @return 启用后的规则
     */
    SensitiveDataRule enableRule(String id);
    
    /**
     * 禁用规则
     * 
     * @param id 规则ID
     * @return 禁用后的规则
     */
    SensitiveDataRule disableRule(String id);
    
    /**
     * 批量更新规则
     * 
     * @param rules 规则列表
     * @return 更新后的规则列表
     */
    List<SensitiveDataRule> batchUpdateRules(List<SensitiveDataRule> rules);
    
    /**
     * 刷新规则到检测服务
     */
    void refreshRules();
    
    /**
     * 获取所有启用的关键词规则
     * 
     * @return 关键词列表
     */
    List<String> getAllEnabledKeywords();
    
    /**
     * 获取所有启用的正则表达式规则
     * 
     * @return 正则表达式列表
     */
    List<String> getAllEnabledRegexPatterns();
}