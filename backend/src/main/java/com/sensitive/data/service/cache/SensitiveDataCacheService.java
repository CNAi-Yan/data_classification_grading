package com.sensitive.data.service.cache;

import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * 敏感数据缓存服务，支持多级缓存
 */
@Service
public class SensitiveDataCacheService {
    
    // 本地缓存管理器
    private final CacheManager caffeineCacheManager;
    
    // Redis缓存管理器
    private final CacheManager redisCacheManager;
    
    // 缓存名称
    private static final String DETECTION_CACHE_NAME = "sensitiveDataDetection";
    
    /**
     * 构造函数
     * @param caffeineCacheManager 本地缓存管理器
     * @param redisCacheManager Redis缓存管理器
     */
    public SensitiveDataCacheService(CacheManager caffeineCacheManager, CacheManager redisCacheManager) {
        this.caffeineCacheManager = caffeineCacheManager;
        this.redisCacheManager = redisCacheManager;
    }
    
    /**
     * 获取缓存值
     * 
     * @param key 缓存键
     * @return 缓存值
     */
    public <T> Optional<T> get(String key, Class<T> type) {
        // 1. 先从本地缓存获取
        Cache caffeineCache = caffeineCacheManager.getCache(DETECTION_CACHE_NAME);
        if (caffeineCache != null) {
            Cache.ValueWrapper caffeineValue = caffeineCache.get(key);
            if (caffeineValue != null) {
                return Optional.ofNullable(type.cast(caffeineValue.get()));
            }
        }
        
        // 2. 本地缓存未命中，从Redis缓存获取
        Cache redisCache = redisCacheManager.getCache(DETECTION_CACHE_NAME);
        if (redisCache != null) {
            Cache.ValueWrapper redisValue = redisCache.get(key);
            if (redisValue != null) {
                T value = type.cast(redisValue.get());
                
                // 将Redis缓存值同步到本地缓存
                if (caffeineCache != null) {
                    caffeineCache.put(key, value);
                }
                
                return Optional.ofNullable(value);
            }
        }
        
        // 3. 缓存未命中
        return Optional.empty();
    }
    
    /**
     * 设置缓存值
     * 
     * @param key 缓存键
     * @param value 缓存值
     */
    public <T> void put(String key, T value) {
        // 1. 设置本地缓存
        Cache caffeineCache = caffeineCacheManager.getCache(DETECTION_CACHE_NAME);
        if (caffeineCache != null) {
            caffeineCache.put(key, value);
        }
        
        // 2. 设置Redis缓存
        Cache redisCache = redisCacheManager.getCache(DETECTION_CACHE_NAME);
        if (redisCache != null) {
            redisCache.put(key, value);
        }
    }
    
    /**
     * 删除缓存值
     * 
     * @param key 缓存键
     */
    public void evict(String key) {
        // 1. 删除本地缓存
        Cache caffeineCache = caffeineCacheManager.getCache(DETECTION_CACHE_NAME);
        if (caffeineCache != null) {
            caffeineCache.evict(key);
        }
        
        // 2. 删除Redis缓存
        Cache redisCache = redisCacheManager.getCache(DETECTION_CACHE_NAME);
        if (redisCache != null) {
            redisCache.evict(key);
        }
    }
    
    /**
     * 清空缓存
     */
    public void clear() {
        // 1. 清空本地缓存
        Cache caffeineCache = caffeineCacheManager.getCache(DETECTION_CACHE_NAME);
        if (caffeineCache != null) {
            caffeineCache.clear();
        }
        
        // 2. 清空Redis缓存
        Cache redisCache = redisCacheManager.getCache(DETECTION_CACHE_NAME);
        if (redisCache != null) {
            redisCache.clear();
        }
    }
    
    /**
     * 检查缓存是否存在
     * 
     * @param key 缓存键
     * @return 是否存在
     */
    public boolean exists(String key) {
        // 1. 检查本地缓存
        Cache caffeineCache = caffeineCacheManager.getCache(DETECTION_CACHE_NAME);
        if (caffeineCache != null) {
            Cache.ValueWrapper caffeineValue = caffeineCache.get(key);
            if (caffeineValue != null) {
                return true;
            }
        }
        
        // 2. 检查Redis缓存
        Cache redisCache = redisCacheManager.getCache(DETECTION_CACHE_NAME);
        if (redisCache != null) {
            Cache.ValueWrapper redisValue = redisCache.get(key);
            if (redisValue != null) {
                return true;
            }
        }
        
        return false;
    }
}