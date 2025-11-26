package com.sensitive.data.service.processor;

import com.sensitive.data.model.SensitiveDataType;

/**
 * 敏感数据处理服务接口
 */
public interface SensitiveDataProcessorService {
    
    /**
     * 获取敏感数据处理建议
     * 
     * @param type 敏感数据类型
     * @return 处理建议
     */
    String getProcessingSuggestion(SensitiveDataType type);
    
    /**
     * 对敏感数据进行脱敏处理
     * 
     * @param data 原始数据
     * @param type 敏感数据类型
     * @return 脱敏后的数据
     */
    String maskSensitiveData(String data, SensitiveDataType type);
}