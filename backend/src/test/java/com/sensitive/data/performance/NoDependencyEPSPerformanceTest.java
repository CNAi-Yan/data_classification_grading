package com.sensitive.data.performance;

/**
 * 无依赖的EPS性能测试类
 */
public class NoDependencyEPSPerformanceTest {
    
    public static void main(String[] args) {
        System.out.println("=== 无依赖EPS性能测试开始 ===");
        
        // 简单的字符串匹配测试，模拟敏感数据检测
        String testData = "张三的身份证号是110101199001011234，手机号是13812345678";
        
        // 运行1000次简单的字符串匹配
        int totalRequests = 1000;
        long startTime = System.currentTimeMillis();
        
        int matches = 0;
        for (int i = 0; i < totalRequests; i++) {
            // 模拟简单的敏感数据检测：检查是否包含身份证号或手机号
            if (testData.contains("身份证号") || testData.contains("手机号")) {
                matches++;
            }
            
            if (i % 100 == 0) {
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
        System.out.println("匹配次数: " + matches);
        System.out.println("总耗时: " + totalTimeMs + "ms");
        System.out.println("每秒处理请求数(EPS): " + requestsPerSecond);
        System.out.println("平均每个请求耗时: " + String.format("%.3f", avgTimePerRequest) + "ms");
        System.out.println("=== 无依赖EPS性能测试结束 ===");
    }
}