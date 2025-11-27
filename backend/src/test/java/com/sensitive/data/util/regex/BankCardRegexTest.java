package com.sensitive.data.util.regex;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 银行卡号正则表达式测试程序
 */
public class BankCardRegexTest {
    
    public static void main(String[] args) {
        // 获取当前的银行卡号正则表达式模式
        Pattern bankCardPattern = RegexPatterns.getPattern(com.sensitive.data.model.SensitiveDataType.BANK_CARD);
        
        // 所有测试样本
        List<String> bankCardSamples = Arrays.asList(
            "6222021234567890123",  // 连续格式
            "4111111111111111",       // 连续格式
            "5555555555554444",       // 连续格式
            "6222 0212 3456 7890 123", // 带空格格式
            "4111 1111 1111 1111",     // 带空格格式
            "6222-0212-3456-7890-123",  // 带连字符格式
            "4111-1111-1111-1111"      // 带连字符格式
        );
        
        System.out.println("=== 银行卡号正则表达式匹配测试 ===");
        System.out.println("当前正则表达式: " + bankCardPattern.pattern());
        System.out.println("测试样本数量: " + bankCardSamples.size());
        System.out.println();
        
        // 测试每个样本
        int matchedCount = 0;
        for (String sample : bankCardSamples) {
            boolean isMatched = bankCardPattern.matcher(sample).find();
            System.out.printf("样本: %-35s 匹配结果: %s%n", sample, isMatched ? "✅ 匹配" : "❌ 不匹配");
            if (isMatched) {
                matchedCount++;
            }
        }
        
        System.out.println();
        System.out.println("=== 测试结果汇总 ===");
        System.out.printf("总样本数: %d, 匹配成功: %d, 匹配失败: %d%n", 
                bankCardSamples.size(), matchedCount, bankCardSamples.size() - matchedCount);
        
        if (matchedCount == bankCardSamples.size()) {
            System.out.println("✅ 所有样本都匹配成功！");
        } else {
            System.out.println("❌ 部分样本匹配失败！");
        }
    }
}