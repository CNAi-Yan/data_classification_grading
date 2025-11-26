package com.sensitive.data.performance;

import org.junit.Test;

import com.sensitive.data.service.detector.SensitiveDataDetectorService;
import com.sensitive.data.service.detector.impl.SensitiveDataDetectorServiceImpl;
import com.sensitive.data.service.processor.impl.SensitiveDataProcessorServiceImpl;

/**
 * 性能测试类，使用JUnit测试框架，确保所有依赖都被正确加载
 */
public class PerformanceTest {
    
    private final SensitiveDataDetectorService detectorService = 
            new SensitiveDataDetectorServiceImpl(new SensitiveDataProcessorServiceImpl());
    
    @Test
    public void testPerformance() {
        // 测试数据
        String testData = "张三的身份证号是110101199001011234，手机号是13812345678，邮箱是zhangsan@example.com";
        
        // 执行多次检测，测试性能
        int iterations = 1000;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < iterations; i++) {
            detectorService.detectSensitiveData(testData);
        }
        
        long endTime = System.currentTimeMillis();
        long totalTimeMs = endTime - startTime;
        double avgTimePerRequest = (double) totalTimeMs / iterations;
        int requestsPerSecond = (int) (1000 / avgTimePerRequest);
        
        System.out.println("=== 敏感数据检测性能测试结果 ===");
        System.out.println("总请求数: " + iterations);
        System.out.println("总耗时: " + totalTimeMs + "ms");
        System.out.println("平均每个请求耗时: " + String.format("%.3f", avgTimePerRequest) + "ms");
        System.out.println("每秒处理请求数: " + requestsPerSecond);
        
        // 验证是否达到要求
        assert requestsPerSecond >= 5000 : "性能未达到要求，每秒处理请求数: " + requestsPerSecond + "，要求: 5000";
    }
}