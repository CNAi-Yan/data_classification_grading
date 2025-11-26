package com.sensitive.data.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sensitive.data.service.detector.impl.SensitiveDataDetectorServiceImpl;
import com.sensitive.data.service.processor.SensitiveDataProcessorService;
import com.sensitive.data.service.processor.impl.SensitiveDataProcessorServiceImpl;

/**
 * 简单的EPS性能测试类
 */
public class SimpleEPSPerformanceTest {
    
    public static void main(String[] args) throws InterruptedException {
        // 初始化服务
        SensitiveDataProcessorService processorService = new SensitiveDataProcessorServiceImpl();
        SensitiveDataDetectorServiceImpl detectorService = new SensitiveDataDetectorServiceImpl(processorService);
        
        // 测试数据样本
        List<String> testDataSamples = new ArrayList<>();
        testDataSamples.add("张三的身份证号是110101199001011234，手机号是13812345678");
        testDataSamples.add("李四的银行卡号是6222021234567890123，邮箱是lisi@example.com");
        testDataSamples.add("王五的护照号是E12345678，驾照号是12345678901234567");
        testDataSamples.add("赵六的密码是Abc123456，银行账号是ABC1234567890123456789");
        testDataSamples.add("这是一段正常的文本，不包含任何敏感信息");
        
        // 测试参数
        int totalRequests = 10000;
        int concurrentThreads = 10;
        int requestsPerThread = totalRequests / concurrentThreads;
        
        System.out.println("=== 敏感数据检测EPS性能测试开始 ===");
        System.out.println("总请求数: " + totalRequests);
        System.out.println("并发线程数: " + concurrentThreads);
        System.out.println("每个线程请求数: " + requestsPerThread);
        
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads);
        CountDownLatch latch = new CountDownLatch(totalRequests);
        
        long startTime = System.currentTimeMillis();
        
        // 提交任务
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    // 随机选择测试数据
                    String testData = testDataSamples.get((int) (Math.random() * testDataSamples.size()));
                    
                    // 执行检测
                    detectorService.detectSensitiveData(testData);
                    
                    latch.countDown();
                }
            });
        }
        
        // 等待所有任务完成
        latch.await();
        
        long endTime = System.currentTimeMillis();
        long totalTimeMs = endTime - startTime;
        long totalTimeSeconds = totalTimeMs / 1000;
        if (totalTimeSeconds == 0) {
            totalTimeSeconds = 1; // 避免除以零
        }
        
        long requestsPerSecond = totalRequests / totalTimeSeconds;
        double avgTimePerRequest = (double) totalTimeMs / totalRequests;
        
        System.out.println("总耗时: " + totalTimeMs + "ms");
        System.out.println("每秒处理请求数(EPS): " + requestsPerSecond);
        System.out.println("平均每个请求耗时: " + String.format("%.3f", avgTimePerRequest) + "ms");
        System.out.println("=== 敏感数据检测EPS性能测试结束 ===");
        
        // 关闭线程池
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }
}