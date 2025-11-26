package com.sensitive.data.service.detector;

import com.sensitive.data.model.SensitiveDataDetectionResult;

/**
 * 敏感数据检测服务接口
 */
public interface SensitiveDataDetectorService {
    
    /**
     * 检测文本中的敏感数据
     * 
     * @param text 待检测的文本
     * @return 检测结果
     */
    SensitiveDataDetectionResult detectSensitiveData(String text);
    
    /**
     * 检测文本中的敏感数据（实时检测，用于WebSocket）
     * 
     * @param text 待检测的文本
     * @return 检测结果
     */
    SensitiveDataDetectionResult detectSensitiveDataRealtime(String text);
}