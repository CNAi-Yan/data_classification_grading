package com.sensitive.data.util;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 简单的对象池实现，用于复用对象，减少GC压力
 * 
 * @param <T> 对象类型
 */
public class ObjectPool<T> {
    
    private final BlockingQueue<T> pool;
    private final ObjectFactory<T> factory;
    private final int maxPoolSize;
    private final int initialPoolSize;
    
    /**
     * 对象工厂接口，用于创建新对象
     */
    public interface ObjectFactory<T> {
        T create();
    }
    
    /**
     * 构造函数
     * 
     * @param factory 对象工厂
     * @param initialPoolSize 初始池大小
     * @param maxPoolSize 最大池大小
     */
    public ObjectPool(ObjectFactory<T> factory, int initialPoolSize, int maxPoolSize) {
        this.factory = factory;
        this.maxPoolSize = maxPoolSize;
        this.initialPoolSize = initialPoolSize;
        this.pool = new LinkedBlockingQueue<>(maxPoolSize);
        
        // 初始化对象池
        initialize();
    }
    
    /**
     * 初始化对象池
     */
    private void initialize() {
        for (int i = 0; i < initialPoolSize; i++) {
            pool.offer(factory.create());
        }
    }
    
    /**
     * 从池中获取对象
     * 
     * @return 对象实例
     */
    public T borrowObject() {
        T object = pool.poll();
        if (object == null) {
            // 如果池为空，创建新对象
            object = factory.create();
        }
        return object;
    }
    
    /**
     * 归还对象到池中
     * 
     * @param object 要归还的对象
     */
    public void returnObject(T object) {
        // 如果池未满，归还对象
        if (pool.size() < maxPoolSize) {
            pool.offer(object);
        }
        // 否则让对象被GC回收
    }
    
    /**
     * 批量归还对象到池中
     * 
     * @param objects 要归还的对象列表
     */
    public void returnObjects(List<T> objects) {
        for (T object : objects) {
            returnObject(object);
        }
    }
    
    /**
     * 获取当前池大小
     * 
     * @return 池大小
     */
    public int size() {
        return pool.size();
    }
}