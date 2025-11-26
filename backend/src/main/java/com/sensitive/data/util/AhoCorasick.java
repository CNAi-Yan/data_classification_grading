package com.sensitive.data.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 优化后的Aho-Corasick算法实现，用于高效的多模式字符串匹配
 */
public class AhoCorasick {
    
    /**
     * 字典树节点
     */
    private static class Node {
        // 子节点映射，键为字符，值为子节点
        // 使用ConcurrentHashMap提高并发性能
        private final Map<Character, Node> children = new ConcurrentHashMap<>();
        // 失败指针
        private volatile Node fail;
        // 匹配的模式串列表（如果有）
        // 使用线程安全的List
        private final List<String> patterns = Collections.synchronizedList(new ArrayList<>());
        // 节点深度
        private final int depth;
        
        /**
         * 构造函数
         * @param depth 节点深度
         */
        public Node(int depth) {
            this.depth = depth;
        }
    }
    
    // 字典树根节点
    private final Node root;
    
    // 读写锁，用于支持并发更新和查询
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    
    // 模式串数量
    private volatile int patternCount;
    
    /**
     * 构造函数，初始化Aho-Corasick算法
     * 
     * @param patterns 模式串列表
     */
    public AhoCorasick(List<String> patterns) {
        root = new Node(0);
        patternCount = 0;
        buildTrie(patterns);
        buildFailPointers();
    }
    
    /**
     * 构造函数，初始化空的Aho-Corasick算法
     */
    public AhoCorasick() {
        root = new Node(0);
        patternCount = 0;
    }
    
    /**
     * 更新模式串列表
     * 
     * @param patterns 新的模式串列表
     */
    public void updatePatterns(List<String> patterns) {
        writeLock.lock();
        try {
            // 重置字典树
            root.children.clear();
            root.patterns.clear();
            root.fail = null;
            patternCount = 0;
            
            // 重新构建字典树和失败指针
            buildTrie(patterns);
            buildFailPointers();
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 构建字典树
     * 
     * @param patterns 模式串列表
     */
    private void buildTrie(List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return;
        }
        
        // 去重，避免重复处理相同的模式串
        Set<String> uniquePatternSet = new HashSet<>();
        uniquePatternSet.addAll(patterns);
        List<String> uniquePatterns = new ArrayList<>(uniquePatternSet);
        
        for (String pattern : uniquePatterns) {
            if (pattern == null || pattern.isEmpty()) {
                continue;
            }
            
            Node current = root;
            for (char c : pattern.toCharArray()) {
                final int parentDepth = current.depth;
                current = current.children.computeIfAbsent(c, k -> new Node(parentDepth + 1));
            }
            
            // 避免重复添加相同的模式串
            if (!current.patterns.contains(pattern)) {
                current.patterns.add(pattern);
                patternCount++;
            }
        }
    }
    
    /**
     * 构建失败指针
     */
    private void buildFailPointers() {
        Deque<Node> queue = new LinkedList<>();
        
        // 初始化根节点的所有子节点的失败指针为根节点
        for (Node child : root.children.values()) {
            child.fail = root;
            queue.offer(child);
        }
        
        // BFS遍历字典树，构建失败指针
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            
            for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
                char c = entry.getKey();
                Node child = entry.getValue();
                queue.offer(child);
                
                // 查找当前节点的失败指针
                Node failNode = current.fail;
                while (failNode != null && !failNode.children.containsKey(c)) {
                    failNode = failNode.fail;
                }
                
                child.fail = (failNode != null) ? failNode.children.get(c) : root;
                if (child.fail == null) {
                    child.fail = root;
                }
                
                // 合并失败指针节点的模式串，避免重复
                if (child.fail != root && !child.fail.patterns.isEmpty()) {
                    synchronized (child.patterns) {
                        for (String pattern : child.fail.patterns) {
                            if (!child.patterns.contains(pattern)) {
                                child.patterns.add(pattern);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 匹配文本，返回所有匹配结果
     * 
     * @param text 要匹配的文本
     * @return 匹配结果列表，每个元素包含匹配的模式串和结束位置
     */
    public List<MatchResult> match(String text) {
        if (text == null || text.isEmpty() || patternCount == 0) {
            return Collections.emptyList();
        }
        
        readLock.lock();
        try {
            List<MatchResult> results = new ArrayList<>();
            Node current = root;
            
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                
                // 沿着失败指针查找匹配的子节点
                while (current != root && !current.children.containsKey(c)) {
                    current = current.fail;
                }
                
                // 如果找到匹配的子节点，移动到该节点
                Node child = current.children.get(c);
                if (child != null) {
                    current = child;
                    
                    // 收集匹配结果
                    if (!current.patterns.isEmpty()) {
                        synchronized (current.patterns) {
                            for (String pattern : current.patterns) {
                                int start = i - pattern.length() + 1;
                                results.add(new MatchResult(pattern, start, i + 1));
                            }
                        }
                    }
                }
            }
            
            return results;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * 批量匹配文本，返回所有匹配结果
     * 
     * @param texts 要匹配的文本列表
     * @return 匹配结果列表，每个元素包含文本索引和对应的匹配结果
     */
    public List<BatchMatchResult> matchBatch(List<String> texts) {
        if (texts == null || texts.isEmpty() || patternCount == 0) {
            return Collections.emptyList();
        }
        
        List<BatchMatchResult> batchResults = new ArrayList<>(texts.size());
        
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            List<MatchResult> results = match(text);
            batchResults.add(new BatchMatchResult(i, results));
        }
        
        return batchResults;
    }
    
    /**
     * 获取模式串数量
     * 
     * @return 模式串数量
     */
    public int getPatternCount() {
        return patternCount;
    }
    
    /**
     * 匹配结果类
     */
    public static class MatchResult {
        // 匹配的模式串
        private final String pattern;
        // 匹配的起始位置（包含）
        private final int start;
        // 匹配的结束位置（不包含）
        private final int end;
        
        public MatchResult(String pattern, int start, int end) {
            this.pattern = pattern;
            this.start = start;
            this.end = end;
        }
        
        public String getPattern() {
            return pattern;
        }
        
        public int getStart() {
            return start;
        }
        
        public int getEnd() {
            return end;
        }
        
        @Override
        public String toString() {
            return "MatchResult{pattern='" + pattern + "', start=" + start + ", end=" + end + "}";
        }
    }
    
    /**
     * 批量匹配结果类
     */
    public static class BatchMatchResult {
        // 文本索引
        private final int textIndex;
        // 匹配结果列表
        private final List<MatchResult> matchResults;
        
        public BatchMatchResult(int textIndex, List<MatchResult> matchResults) {
            this.textIndex = textIndex;
            this.matchResults = matchResults;
        }
        
        public int getTextIndex() {
            return textIndex;
        }
        
        public List<MatchResult> getMatchResults() {
            return matchResults;
        }
        
        @Override
        public String toString() {
            return "BatchMatchResult{textIndex=" + textIndex + ", matchResults=" + matchResults + "}";
        }
    }
}