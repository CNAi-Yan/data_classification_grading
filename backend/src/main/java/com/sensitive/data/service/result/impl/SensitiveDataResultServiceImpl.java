package com.sensitive.data.service.result.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sensitive.data.model.SensitiveDataDetectionResult;
import com.sensitive.data.model.SensitiveDataType;
import com.sensitive.data.service.result.SensitiveDataResultService;

/**
 * 敏感数据检测结果服务实现
 */
@Service
public class SensitiveDataResultServiceImpl implements SensitiveDataResultService {
    
    // 结果存储，使用ConcurrentHashMap保证线程安全
    private final Map<String, SensitiveDataDetectionResult> resultStore = new ConcurrentHashMap<>();
    
    @Override
    public SensitiveDataDetectionResult saveResult(SensitiveDataDetectionResult result) {
        // 生成结果ID
        String id = result.id() != null ? result.id() : generateResultId(result);
        
        // 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = result.createdAt() != null ? result.createdAt() : now;
        
        // 记录类是不可变的，需要创建新的实例
        SensitiveDataDetectionResult updatedResult = new SensitiveDataDetectionResult(
            id,
            result.originalText(),
            result.detectedItems(),
            result.totalDetected(),
            result.processingTimeMs(),
            createdAt,
            now
        );
        
        // 保存结果
        resultStore.put(id, updatedResult);
        
        return updatedResult;
    }
    
    @Override
    public List<SensitiveDataDetectionResult> saveResults(List<SensitiveDataDetectionResult> results) {
        List<SensitiveDataDetectionResult> savedResults = new ArrayList<>();
        
        for (SensitiveDataDetectionResult result : results) {
            savedResults.add(saveResult(result));
        }
        
        return savedResults;
    }
    
    @Override
    public Optional<SensitiveDataDetectionResult> getResult(String id) {
        return Optional.ofNullable(resultStore.get(id));
    }
    
    @Override
    public List<SensitiveDataDetectionResult> getAllResults() {
        return new ArrayList<>(resultStore.values());
    }
    
    @Override
    public List<SensitiveDataDetectionResult> getResultsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return resultStore.values().stream()
                .filter(result -> result.createdAt().isAfter(startTime) && result.createdAt().isBefore(endTime))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SensitiveDataDetectionResult> getResultsBySensitiveDataType(SensitiveDataType type) {
        return resultStore.values().stream()
                .filter(result -> result.detectedItems() != null)
                .filter(result -> result.detectedItems().stream()
                        .anyMatch(item -> item.type() == type))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SensitiveDataDetectionResult> getResultsBySensitiveCount(int minCount, int maxCount) {
        return resultStore.values().stream()
                .filter(result -> result.totalDetected() >= minCount && result.totalDetected() <= maxCount)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteResult(String id) {
        resultStore.remove(id);
    }
    
    @Override
    public void clearAllResults() {
        resultStore.clear();
    }
    
    @Override
    public long countResults() {
        return resultStore.size();
    }
    
    @Override
    public long countResultsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return getResultsByTimeRange(startTime, endTime).size();
    }
    
    @Override
    public long countResultsBySensitiveDataType(SensitiveDataType type) {
        return getResultsBySensitiveDataType(type).size();
    }
    
    /**
     * 生成结果ID
     * @param result 检测结果
     * @return 结果ID
     */
    private String generateResultId(SensitiveDataDetectionResult result) {
        String prefix = "RESULT_";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = String.valueOf(result.hashCode());
        return prefix + timestamp + "_" + suffix;
    }
}