package com.sensitive.data.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.sensitive.data.service.detector.SensitiveDataDetectorService;
import com.sensitive.data.service.detector.impl.SensitiveDataDetectorServiceImpl;
import com.sensitive.data.service.processor.impl.SensitiveDataProcessorServiceImpl;

/**
 * 高性能测试类，验证每秒5000条消息的处理能力
 */
public class HighPerformanceTest {
    
    // 测试配置
    private static final int TOTAL_REQUESTS = 50000; // 总请求数
    private static final int CONCURRENT_THREADS = 50; // 并发线程数
    private static final int REQUESTS_PER_THREAD = TOTAL_REQUESTS / CONCURRENT_THREADS; // 每个线程处理的请求数
    
    // 测试数据样本
    private static final List<String> TEST_DATA_SAMPLES = new ArrayList<>();
    
    static {
        // 添加各种类型的测试数据
        TEST_DATA_SAMPLES.add("张三的身份证号是110101199001011234，手机号是13812345678");
        TEST_DATA_SAMPLES.add("李四的银行卡号是6222021234567890123，邮箱是lisi@example.com");
        TEST_DATA_SAMPLES.add("王五的护照号是E12345678，驾照号是12345678901234567");
        TEST_DATA_SAMPLES.add("赵六的密码是Abc123456，银行账号是ABC1234567890123456789");
        TEST_DATA_SAMPLES.add("这是一段正常的文本，不包含任何敏感信息");
        TEST_DATA_SAMPLES.add("政治敏感词测试：台独、法轮功、颠覆政府");
        TEST_DATA_SAMPLES.add("色情词汇测试：色情、黄色、成人");
        TEST_DATA_SAMPLES.add("暴力词汇测试：暴力、杀人、血腥");
        TEST_DATA_SAMPLES.add("版权词汇测试：盗版、侵权、破解版");
        TEST_DATA_SAMPLES.add("混合敏感信息：张三的身份证号是110101199001011234，手机号是13812345678，银行卡号是6222021234567890123");
    }
    
    public static void main(String[] args) throws InterruptedException {
        // 初始化检测服务
        SensitiveDataDetectorService detectorService = new SensitiveDataDetectorServiceImpl(
                new SensitiveDataProcessorServiceImpl());
        
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        
        // 原子变量，用于统计成功处理的请求数
        AtomicLong successfulRequests = new AtomicLong(0);
        AtomicLong failedRequests = new AtomicLong(0);
        
        // 用于等待所有线程完成
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);
        
        // 开始时间
        long startTime = System.currentTimeMillis();
        
        // 启动测试
        System.out.println("=== 高性能测试开始 ===");
        System.out.println("总请求数: " + TOTAL_REQUESTS);
        System.out.println("并发线程数: " + CONCURRENT_THREADS);
        System.out.println("每个线程处理请求数: " + REQUESTS_PER_THREAD);
        System.out.println("开始时间: " + startTime);
        
        // 提交任务到线程池
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < REQUESTS_PER_THREAD; j++) {
                        // 随机选择一个测试数据样本
                        String testData = TEST_DATA_SAMPLES.get((int) (Math.random() * TEST_DATA_SAMPLES.size()));
                        
                        try {
                            // 执行检测
                            detectorService.detectSensitiveData(testData);
                            successfulRequests.incrementAndGet();
                        } catch (Exception e) {
                            failedRequests.incrementAndGet();
                            System.err.println("检测失败: " + e.getMessage());
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有线程完成
        latch.await();
        
        // 结束时间
        long endTime = System.currentTimeMillis();
        
        // 计算处理时间
        long processingTime = endTime - startTime;
        double processingTimeSeconds = processingTime / 1000.0;
        
        // 计算吞吐量
        double requestsPerSecond = TOTAL_REQUESTS / processingTimeSeconds;
        
        // 打印测试结果
        System.out.println("=== 高性能测试结果 ===");
        System.out.println("总请求数: " + TOTAL_REQUESTS);
        System.out.println("成功请求数: " + successfulRequests.get());
        System.out.println("失败请求数: " + failedRequests.get());
        System.out.println("开始时间: " + startTime);
        System.out.println("结束时间: " + endTime);
        System.out.println("处理时间: " + processingTime + " 毫秒");
        System.out.println("处理时间: " + processingTimeSeconds + " 秒");
        System.out.println("吞吐量: " + requestsPerSecond + " 请求/秒");
        System.out.println("平均响应时间: " + (processingTime / (double) TOTAL_REQUESTS) + " 毫秒/请求");
        
        // 验证是否达到要求
        if (requestsPerSecond >= 5000) {
            System.out.println("✅ 性能测试通过！每秒处理请求数: " + requestsPerSecond + "，达到要求的5000");
        } else {
            System.out.println("❌ 性能测试未通过！每秒处理请求数: " + requestsPerSecond + "，未达到要求的5000");
        }
        
        // 关闭线程池
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }
}