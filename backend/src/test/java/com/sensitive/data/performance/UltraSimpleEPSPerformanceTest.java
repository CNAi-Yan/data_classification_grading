package com.sensitive.data.performance;

import com.sensitive.data.service.detector.impl.SensitiveDataDetectorServiceImpl;
import com.sensitive.data.service.processor.SensitiveDataProcessorService;
import com.sensitive.data.service.processor.impl.SensitiveDataProcessorServiceImpl;

/**
 * 超简单的EPS性能测试类
 */
public class UltraSimpleEPSPerformanceTest {
    
    public static void main(String[] args) {
        System.out.println("=== 超简单EPS性能测试开始 ===");
        
        // 初始化服务
        SensitiveDataProcessorService processorService = new SensitiveDataProcessorServiceImpl();
        SensitiveDataDetectorServiceImpl detectorService = new SensitiveDataDetectorServiceImpl(processorService);
        
        // 测试数据
        String testData = "张三的身份证号是110101199001011234，手机号是13812345678";
        
        // 运行100次检测
        int totalRequests = 100;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < totalRequests; i++) {
            detectorService.detectSensitiveData(testData);
            if (i % 10 == 0) {
                System.out.println("已完成 " + (i + 1) + " 次检测");
            }
        }
        
        long endTime = System.currentTimeMillis();
        long totalTimeMs = endTime - startTime;
        long totalTimeSeconds = totalTimeMs / 1000;
        if (totalTimeSeconds == 0) {
            totalTimeSeconds = 1; // 避免除以零
        }
        
        long requestsPerSecond = totalRequests / totalTimeSeconds;
        double avgTimePerRequest = (double) totalTimeMs / totalRequests;
        
        System.out.println("总请求数: " + totalRequests);
        System.out.println("总耗时: " + totalTimeMs + "ms");
        System.out.println("每秒处理请求数(EPS): " + requestsPerSecond);
        System.out.println("平均每个请求耗时: " + String.format("%.3f", avgTimePerRequest) + "ms");
        System.out.println("=== 超简单EPS性能测试结束 ===");
    }
}