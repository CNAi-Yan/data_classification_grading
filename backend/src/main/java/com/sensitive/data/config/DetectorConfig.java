package com.sensitive.data.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 敏感数据检测服务配置类
 */
@Configuration
public class DetectorConfig {

    @Value("${sensitive.data.detector.thread-pool.core-size:10}")
    private int corePoolSize;

    @Value("${sensitive.data.detector.thread-pool.max-size:50}")
    private int maxPoolSize;

    @Value("${sensitive.data.detector.thread-pool.queue-capacity:1000}")
    private int queueCapacity;

    @Value("${sensitive.data.detector.thread-pool.keep-alive-time:60s}")
    private Duration keepAliveTime;

    /**
     * 为敏感数据检测服务创建并配置一个线程池执行器。
     *
     * 该执行器使用配置的核心池大小、最大池大小、队列容量和线程存活时间；线程名前缀为 "Detector-Thread-"，
     * 拒绝策略为 CallerRunsPolicy（线程池和队列满时由调用线程执行任务）。
     *
     * @return 已初始化的 Executor 实例（ThreadPoolTaskExecutor），用于提交检测任务
     */
    @Bean("detectorThreadPool")
    public Executor detectorThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds((int) keepAliveTime.getSeconds());
        executor.setThreadNamePrefix("Detector-Thread-");
        
        // 拒绝策略：当线程池满时，由调用线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        executor.initialize();
        return executor;
    }
}