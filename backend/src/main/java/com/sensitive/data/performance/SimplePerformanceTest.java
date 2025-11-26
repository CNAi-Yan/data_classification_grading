package com.sensitive.data.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sensitive.data.service.detector.SensitiveDataDetectorService;
import com.sensitive.data.service.detector.impl.SensitiveDataDetectorServiceImpl;
import com.sensitive.data.service.processor.impl.SensitiveDataProcessorServiceImpl;

/**
 * 简单的性能测试类，直接在main方法中运行测试
 */
public class SimplePerformanceTest {
    
    // 测试数据样本
    private static final List<String> TEST_DATA_SAMPLES = new ArrayList<>();
    
    static {
        // 添加各种类型的测试数据
        TEST_DATA_SAMPLES.add("张三的身份证号是110101199001011234，手机号是13812345678");
        TEST_DATA_SAMPLES.add("李四的银行卡号是6222021234567890123，邮箱是lisi@example.com");
        TEST_DATA_SAMPLES.add("王五的护照号是E12345678，驾照号是12345678901234567");
        TEST_DATA_SAMPLES.add("赵六的密码是Abc123456，银行账号是ABC1234567890123456789");
        TEST_DATA_SAMPLES.add("这是一段正常的文本，不包含任何敏感信息");
        TEST_DATA_SAMPLES.add("敏感关键词测试：台独、法轮功、色情、暴力");
        TEST_DATA_SAMPLES.add("混合敏感数据：110101199001011234 13812345678 zhangsan@example.com");
        TEST_DATA_SAMPLES.add("大量重复数据：13812345678 13812345678 13812345678 13812345678");
        
        // 使用Java 8兼容的方式生成长文本
        StringBuilder longText = new StringBuilder("长文本测试：");
        String repeatedText = "这是一段很长的文本，包含各种敏感数据，比如身份证号110101199001011234，手机号13812345678，邮箱zhangsan@example.com，银行卡号6222021234567890123，以及敏感关键词台独、法轮功、色情、暴力。";
        for (int i = 0; i < 10; i++) {
            longText.append(repeatedText);
        }
        TEST_DATA_SAMPLES.add(longText.toString());
        TEST_DATA_SAMPLES.add("短文本测试：13812345678");
    }
    
    public static void main(String[] args) throws InterruptedException {
        // 创建检测器服务实例
        SensitiveDataDetectorService detectorService = 
                new SensitiveDataDetectorServiceImpl(new SensitiveDataProcessorServiceImpl());
        
        // 测试参数
        int totalRequests = 50000; // 总请求数
        int concurrentThreads = 10; // 并发线程数
        int requestsPerThread = totalRequests / concurrentThreads;
        
        System.out.println("=== 敏感数据检测性能测试开始 ===");
        System.out.println("总请求数: " + totalRequests);
        System.out.println("并发线程数: " + concurrentThreads);
        System.out.println("每个线程请求数: " + requestsPerThread);
        
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads);
        final CountDownLatch latch = new CountDownLatch(totalRequests);
        
        long startTime = System.currentTimeMillis();
        
        // 提交任务
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    // 随机选择测试数据
                    String testData = TEST_DATA_SAMPLES.get((int) (Math.random() * TEST_DATA_SAMPLES.size()));
                    
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
        System.out.println("每秒处理请求数: " + requestsPerSecond);
        System.out.println("平均每个请求耗时: " + String.format("%.3f", avgTimePerRequest) + "ms");
        System.out.println("=== 敏感数据检测性能测试结束 ===");
        
        // 验证是否达到要求
        if (requestsPerSecond >= 5000) {
            System.out.println("✅ 性能测试通过！每秒处理请求数: " + requestsPerSecond + "，达到要求的5000");
        } else {
            System.out.println("❌ 性能测试未通过！每秒处理请求数: " + requestsPerSecond + "，未达到要求的5000");
        }
        
        // 关闭线程池
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        
        // 测试实时检测性能
        System.out.println("\n=== 实时敏感数据检测性能测试开始 ===");
        
        final CountDownLatch realtimeLatch = new CountDownLatch(totalRequests);
        startTime = System.currentTimeMillis();
        
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    String testData = TEST_DATA_SAMPLES.get((int) (Math.random() * TEST_DATA_SAMPLES.size()));
                    detectorService.detectSensitiveDataRealtime(testData);
                    realtimeLatch.countDown();
                }
            });
        }
        
        // 等待所有任务完成
        realtimeLatch.await();
        
        endTime = System.currentTimeMillis();
        totalTimeMs = endTime - startTime;
        totalTimeSeconds = totalTimeMs / 1000;
        if (totalTimeSeconds == 0) {
            totalTimeSeconds = 1;
        }
        
        requestsPerSecond = totalRequests / totalTimeSeconds;
        avgTimePerRequest = (double) totalTimeMs / totalRequests;
        
        System.out.println("总耗时: " + totalTimeMs + "ms");
        System.out.println("每秒处理请求数: " + requestsPerSecond);
        System.out.println("平均每个请求耗时: " + String.format("%.3f", avgTimePerRequest) + "ms");
        System.out.println("=== 实时敏感数据检测性能测试结束 ===");
        
        // 验证是否达到要求
        if (requestsPerSecond >= 5000) {
            System.out.println("✅ 实时性能测试通过！每秒处理请求数: " + requestsPerSecond + "，达到要求的5000");
        } else {
            System.out.println("❌ 实时性能测试未通过！每秒处理请求数: " + requestsPerSecond + "，未达到要求的5000");
        }
        
        // 关闭线程池
        executorService.shutdown();
    }
}