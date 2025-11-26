package com.sensitive.data.util;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 性能监控工具类，用于记录和统计敏感数据检测的性能指标
 */
public class PerformanceMonitor {
    
    // 总请求数
    private final LongAdder totalRequests = new LongAdder();
    // 成功请求数
    private final LongAdder successfulRequests = new LongAdder();
    // 失败请求数
    private final LongAdder failedRequests = new LongAdder();
    // 总处理时间（毫秒）
    private final AtomicLong totalProcessingTime = new AtomicLong(0);
    // 最大处理时间（毫秒）
    private final AtomicLong maxProcessingTime = new AtomicLong(0);
    // 最小处理时间（毫秒）
    private final AtomicLong minProcessingTime = new AtomicLong(Long.MAX_VALUE);
    // 检测到的敏感数据项总数
    private final LongAdder totalDetectedItems = new LongAdder();
    
    /**
     * 记录一次检测请求
     * 
     * @param processingTime 处理时间（毫秒）
     * @param detectedItemsCount 检测到的敏感数据项数量
     * @param success 是否成功
     */
    public void recordRequest(long processingTime, int detectedItemsCount, boolean success) {
        totalRequests.increment();
        
        if (success) {
            successfulRequests.increment();
        } else {
            failedRequests.increment();
        }
        
        totalProcessingTime.addAndGet(processingTime);
        
        // 更新最大处理时间
        updateMaxProcessingTime(processingTime);
        
        // 更新最小处理时间
        updateMinProcessingTime(processingTime);
        
        totalDetectedItems.add(detectedItemsCount);
    }
    
    /**
     * 更新最大处理时间
     */
    private void updateMaxProcessingTime(long time) {
        long currentMax;
        do {
            currentMax = maxProcessingTime.get();
            if (time <= currentMax) {
                break;
            }
        } while (!maxProcessingTime.compareAndSet(currentMax, time));
    }
    
    /**
     * 更新最小处理时间
     */
    private void updateMinProcessingTime(long time) {
        long currentMin;
        do {
            currentMin = minProcessingTime.get();
            if (time >= currentMin) {
                break;
            }
        } while (!minProcessingTime.compareAndSet(currentMin, time));
    }
    
    /**
     * 获取平均处理时间
     * 
     * @return 平均处理时间（毫秒）
     */
    public double getAverageProcessingTime() {
        long requests = totalRequests.sum();
        return requests > 0 ? (double) totalProcessingTime.get() / requests : 0;
    }
    
    /**
     * 获取最大处理时间
     * 
     * @return 最大处理时间（毫秒）
     */
    public long getMaxProcessingTime() {
        return maxProcessingTime.get();
    }
    
    /**
     * 获取最小处理时间
     * 
     * @return 最小处理时间（毫秒）
     */
    public long getMinProcessingTime() {
        long min = minProcessingTime.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }
    
    /**
     * 获取总请求数
     * 
     * @return 总请求数
     */
    public long getTotalRequests() {
        return totalRequests.sum();
    }
    
    /**
     * 获取成功请求数
     * 
     * @return 成功请求数
     */
    public long getSuccessfulRequests() {
        return successfulRequests.sum();
    }
    
    /**
     * 获取失败请求数
     * 
     * @return 失败请求数
     */
    public long getFailedRequests() {
        return failedRequests.sum();
    }
    
    /**
     * 获取总检测到的敏感数据项数量
     * 
     * @return 总检测到的敏感数据项数量
     */
    public long getTotalDetectedItems() {
        return totalDetectedItems.sum();
    }
    
    /**
     * 获取平均每次请求检测到的敏感数据项数量
     * 
     * @return 平均每次请求检测到的敏感数据项数量
     */
    public double getAverageDetectedItemsPerRequest() {
        long requests = totalRequests.sum();
        return requests > 0 ? (double) totalDetectedItems.sum() / requests : 0;
    }
    
    /**
     * 重置所有统计数据
     */
    public void reset() {
        totalRequests.reset();
        successfulRequests.reset();
        failedRequests.reset();
        totalProcessingTime.set(0);
        maxProcessingTime.set(0);
        minProcessingTime.set(Long.MAX_VALUE);
        totalDetectedItems.reset();
    }
    
    /**
     * 获取性能统计信息
     * 
     * @return 性能统计信息字符串
     */
    @Override
    public String toString() {
        return "PerformanceStats{" +
                "totalRequests=" + getTotalRequests() +
                ", successfulRequests=" + getSuccessfulRequests() +
                ", failedRequests=" + getFailedRequests() +
                ", avgProcessingTime=" + String.format("%.2f", getAverageProcessingTime()) + "ms" +
                ", maxProcessingTime=" + getMaxProcessingTime() + "ms" +
                ", minProcessingTime=" + getMinProcessingTime() + "ms" +
                ", totalDetectedItems=" + getTotalDetectedItems() +
                ", avgDetectedItemsPerRequest=" + String.format("%.2f", getAverageDetectedItemsPerRequest()) +
                '}';
    }
}