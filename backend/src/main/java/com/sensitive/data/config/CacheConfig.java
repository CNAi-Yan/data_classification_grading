package com.sensitive.data.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 缓存配置类，配置多级缓存
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    // 本地缓存过期时间
    @Value("${sensitive.data.detector.cache.ttl:3600s}")
    private Duration localCacheTtl;
    
    // 本地缓存最大大小
    @Value("${sensitive.data.detector.cache.max-size:100000}")
    private long localCacheMaxSize;
    
    // Redis缓存过期时间（秒）
    @Value("${sensitive.data.detector.cache.redis-ttl:86400}")
    private long redisCacheTtl;
    
    /**
     * 创建并配置一个用于本地缓存的 Caffeine CacheManager。
     *
     * @return 已配置的 Caffeine CacheManager，包含过期策略（基于 localCacheTtl）和最大容量限制（localCacheMaxSize），并开启统计信息记录
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(localCacheTtl)
                .maximumSize(localCacheMaxSize)
                .recordStats());
        return cacheManager;
    }
    
    /**
     * 配置Redis分布式缓存
     * @param connectionFactory Redis连接工厂
     * @return Redis缓存管理器
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(connectionFactory);
        
        builder.cacheDefaults(org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(java.time.Duration.ofSeconds(redisCacheTtl))
                .serializeKeysWith(org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig().getKeySerializationPair())
                .serializeValuesWith(org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig().getValueSerializationPair())
                .disableCachingNullValues());
        
        return builder.build();
    }
}