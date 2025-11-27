package com.sensitive.data.util;

/**
 * Luhn算法验证测试程序
 */
public class LuhnValidatorTest {
    
    public static void main(String[] args) {
        // 测试用例中的卡号
        String[] testCards = {
            "6222021234567890123",  // 连续格式
            "4111111111111111",       // 连续格式
            "5555555555554444",       // 连续格式
            "6222 0212 3456 7890 123", // 带空格格式
            "4111 1111 1111 1111",     // 带空格格式
            "6222-0212-3456-7890-123",  // 带连字符格式
            "4111-1111-1111-1111"      // 带连字符格式
        };
        
        System.out.println("=== Luhn算法验证测试 ===");
        for (String card : testCards) {
            boolean isValid = LuhnUtil.isValidCardNumber(card);
            System.out.printf("卡号: %-35s Luhn验证: %s\n", card, isValid ? "✅ 有效" : "❌ 无效");
        }
    }
}