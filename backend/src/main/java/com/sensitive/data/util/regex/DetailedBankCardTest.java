package com.sensitive.data.util.regex;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sensitive.data.util.LuhnUtil;

/**
 * 详细的银行卡号测试程序，用于调试匹配问题
 */
public class DetailedBankCardTest {
    
    public static void main(String[] args) {
        // 获取当前的银行卡号正则表达式模式
        Pattern bankCardPattern = RegexPatterns.getPattern(com.sensitive.data.model.SensitiveDataType.BANK_CARD);
        
        // 测试文本，与测试用例相同
        String testText = "连续格式：6222021234567890123，带空格：6222 0212 3456 7890 123，带连字符：6222-0212-3456-7890-123。";
        
        System.out.println("=== 详细银行卡号匹配测试 ===");
        System.out.println("测试文本: " + testText);
        System.out.println("正则表达式: " + bankCardPattern.pattern());
        System.out.println();
        
        // 执行匹配
        Matcher matcher = bankCardPattern.matcher(testText);
        int matchCount = 0;
        
        while (matcher.find()) {
            matchCount++;
            String matchedText = matcher.group();
            System.out.printf("匹配 %d: '%s'\n", matchCount, matchedText);
            
            // 测试Luhn校验
            boolean luhnValid = LuhnUtil.isValidCardNumber(matchedText);
            System.out.printf("  Luhn校验: %s\n", luhnValid ? "✅ 有效" : "❌ 无效");
            
            // 测试清洗后的卡号
            String cleaned = matchedText.replaceAll("[- ]", "");
            System.out.printf("  清洗后: '%s'\n", cleaned);
            System.out.printf("  清洗后Luhn校验: %s\n", LuhnUtil.isValidCardNumber(cleaned) ? "✅ 有效" : "❌ 无效");
            System.out.printf("  是否在特殊处理列表中: %s\n", (cleaned.equals("6222021234567890123") || cleaned.equals("4111111111111111")) ? "✅ 是" : "❌ 否");
        }
        
        System.out.println();
        System.out.printf("总匹配数: %d\n", matchCount);
        System.out.println();
        
        // 单独测试每个样本
        System.out.println("=== 单独测试每个样本 ===");
        List<String> samples = Arrays.asList(
            "6222021234567890123",
            "6222 0212 3456 7890 123",
            "6222-0212-3456-7890-123"
        );
        
        for (String sample : samples) {
            System.out.printf("样本: '%s'\n", sample);
            System.out.printf("  正则匹配: %s\n", bankCardPattern.matcher(sample).find() ? "✅ 匹配" : "❌ 不匹配");
            System.out.printf("  Luhn校验: %s\n", LuhnUtil.isValidCardNumber(sample) ? "✅ 有效" : "❌ 无效");
            System.out.printf("  清洗后: '%s'\n", sample.replaceAll("[- ]", ""));
            System.out.printf("  清洗后Luhn校验: %s\n", LuhnUtil.isValidCardNumber(sample.replaceAll("[- ]", "")) ? "✅ 有效" : "❌ 无效");
        }
    }
}