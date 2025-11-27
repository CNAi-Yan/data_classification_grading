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
    
    /**
     * 保存或更新单个敏感数据检测结果并返回已保存的不可变副本。
     *
     * <p>如果输入结果缺少 ID，则会为其生成一个唯一 ID；如果缺少创建时间，则将创建时间设为当前时间；更新时戳（updatedAt）总是设为当前时间。方法将返回包含这些确定字段的新的不可变结果实例，并将其存入内部存储。</p>
     *
     * @param result 要保存的敏感数据检测结果实例（可能缺少 id 或 createdAt）
     * @return 包含确定 id、createdAt（若输入缺失则为当前时间）及 updatedAt（当前时间）的已保存 {@code SensitiveDataDetectionResult} 实例
     */
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
    
    /**
     * 将多个检测结果保存到服务的内部存储并返回保存后的结果集合。
     *
     * 每个输入结果将通过 saveResult 进行处理，因此可能会被分配或规范化其 id、createdAt 和 updatedAt 字段。
     *
     * @param results 待保存的检测结果列表；列表中的每一项会被独立保存并返回其已保存的副本
     * @return 保存后的检测结果列表，包含已确定的 id、createdAt 和 updatedAt 字段
     */
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
    
    /**
     * 获取在指定时间区间内创建的检测结果（时间边界为排他）。
     *
     * @param startTime 查询区间的起始时间（排他，结果的 createdAt 必须晚于此时间）
     * @param endTime   查询区间的结束时间（排他，结果的 createdAt 必须早于此时间）
     * @return 包含所有 createdAt 在 (startTime, endTime) 范围内的 SensitiveDataDetectionResult 列表
     */
    @Override
    public List<SensitiveDataDetectionResult> getResultsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return resultStore.values().stream()
                .filter(result -> result.createdAt().isAfter(startTime) && result.createdAt().isBefore(endTime))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据敏感数据类型筛选并返回包含该类型检测项的检测结果。
     *
     * @param type 要匹配的敏感数据类型
     * @return 包含至少一个类型为指定 `type` 的检测项的 SensitiveDataDetectionResult 列表
     */
    @Override
    public List<SensitiveDataDetectionResult> getResultsBySensitiveDataType(SensitiveDataType type) {
        return resultStore.values().stream()
                .filter(result -> result.detectedItems() != null)
                .filter(result -> result.detectedItems().stream()
                        .anyMatch(item -> item.type() == type))
                .collect(Collectors.toList());
    }
    
    /**
     * 根据检测到的敏感项数量范围筛选并返回匹配的结果。
     *
     * @param minCount 最小检测数量（包含）
     * @param maxCount 最大检测数量（包含）
     * @return 包含 totalDetected 在 [minCount, maxCount] 范围内的 SensitiveDataDetectionResult 列表
     */
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