package com.sensitive.data.config;

import java.util.concurrent.TimeUnit;

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
 * 缓存配置类，配置多级缓存（本地缓存 + Redis分布式缓存）
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    // 本地缓存过期时间（秒）
    @Value("${sensitive.data.detector.cache.ttl:3600}")
    private long localCacheTtl;
    
    // 本地缓存最大大小
    @Value("${sensitive.data.detector.cache.max-size:100000}")
    private long localCacheMaxSize;
    
    // Redis缓存过期时间（秒）
    @Value("${sensitive.data.detector.cache.redis-ttl:86400}")
    private long redisCacheTtl;
    
    /**
     * 配置Caffeine本地缓存（一级缓存）
     * @return Caffeine缓存管理器
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(localCacheTtl, TimeUnit.SECONDS)
                .maximumSize(localCacheMaxSize)
                .recordStats());
        return cacheManager;
    }
    
    /**
     * 配置Redis分布式缓存（二级缓存）
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
