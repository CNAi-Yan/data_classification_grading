package com.sensitive.data.service.detector;

import java.util.List;

import com.sensitive.data.model.SensitiveDataDetectionResult;

import reactor.core.publisher.Mono;

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
     * 异步检测文本中的敏感数据
     * 
     * @param text 待检测的文本
     * @return 异步检测结果
     */
    Mono<SensitiveDataDetectionResult> detectSensitiveDataAsync(String text);
    
    /**
     * 批量检测文本中的敏感数据
     * 
     * @param texts 待检测的文本列表
     * @return 检测结果列表
     */
    Mono<List<SensitiveDataDetectionResult>> detectSensitiveDataBatch(List<String> texts);
    
    /**
     * 检测文本中的敏感数据（实时检测，用于WebSocket）
     * 
     * @param text 待检测的文本
     * @return 检测结果
     */
    SensitiveDataDetectionResult detectSensitiveDataRealtime(String text);
}