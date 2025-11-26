package com.sensitive.data.service.result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.sensitive.data.model.SensitiveDataDetectionResult;
import com.sensitive.data.model.SensitiveDataType;

/**
 * 敏感数据检测结果服务接口
 */
public interface SensitiveDataResultService {
    
    /**
     * 存储检测结果
     * 
     * @param result 检测结果
     * @return 存储后的结果
     */
    SensitiveDataDetectionResult saveResult(SensitiveDataDetectionResult result);
    
    /**
     * 批量存储检测结果
     * 
     * @param results 检测结果列表
     * @return 存储后的结果列表
     */
    List<SensitiveDataDetectionResult> saveResults(List<SensitiveDataDetectionResult> results);
    
    /**
     * 获取检测结果
     * 
     * @param id 结果ID
     * @return 检测结果
     */
    Optional<SensitiveDataDetectionResult> getResult(String id);
    
    /**
     * 获取所有检测结果
     * 
     * @return 检测结果列表
     */
    List<SensitiveDataDetectionResult> getAllResults();
    
    /**
     * 根据时间范围获取检测结果
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 检测结果列表
     */
    List<SensitiveDataDetectionResult> getResultsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据敏感数据类型获取检测结果
     * 
     * @param type 敏感数据类型
     * @return 检测结果列表
     */
    List<SensitiveDataDetectionResult> getResultsBySensitiveDataType(SensitiveDataType type);
    
    /**
     * 根据检测到的敏感数据数量获取结果
     * 
     * @param minCount 最小数量
     * @param maxCount 最大数量
     * @return 检测结果列表
     */
    List<SensitiveDataDetectionResult> getResultsBySensitiveCount(int minCount, int maxCount);
    
    /**
     * 删除检测结果
     * 
     * @param id 结果ID
     */
    void deleteResult(String id);
    
    /**
     * 清空所有检测结果
     */
    void clearAllResults();
    
    /**
     * 统计检测结果数量
     * 
     * @return 检测结果数量
     */
    long countResults();
    
    /**
     * 统计指定时间范围内的检测结果数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 检测结果数量
     */
    long countResultsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计指定敏感数据类型的检测结果数量
     * 
     * @param type 敏感数据类型
     * @return 检测结果数量
     */
    long countResultsBySensitiveDataType(SensitiveDataType type);
}