package com.sensitive.data.util.regex;

import com.sensitive.data.model.SensitiveDataType;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * 正则表达式测试类
 */
public class RegexPatternsTest {

    @Test
    public void testBankCardRegex() {
        Pattern pattern = RegexPatterns.getPattern(SensitiveDataType.BANK_CARD);
        
        // 测试连续数字格式
        assertTrue(pattern.matcher("6222021234567890123").find());
        assertTrue(pattern.matcher("4111111111111111").find());
        assertTrue(pattern.matcher("5555555555554444").find());
        
        // 测试带空格分隔的格式
        assertTrue(pattern.matcher("6222 0212 3456 7890 123").find());
        assertTrue(pattern.matcher("4111 1111 1111 1111").find());
        
        // 测试带连字符分隔的格式
        assertTrue(pattern.matcher("6222-0212-3456-7890-123").find());
        assertTrue(pattern.matcher("4111-1111-1111-1111").find());
        
        // 测试无效格式
        assertFalse(pattern.matcher("1234567890").find()); // 太短
        assertFalse(pattern.matcher("123456789012345678901").find()); // 太长
    }

    @Test
    public void testBankAccountRegex() {
        Pattern pattern = RegexPatterns.getPattern(SensitiveDataType.BANK_ACCOUNT);
        
        // 测试连续数字格式
        assertTrue(pattern.matcher("1234567890123456").find());
        assertTrue(pattern.matcher("98765432109876543210").find());
        
        // 测试带字母的连续格式
        assertTrue(pattern.matcher("AB1234567890123456").find());
        assertTrue(pattern.matcher("XYZ123456789012345678").find());
        
        // 测试带空格分隔的格式
        assertTrue(pattern.matcher("1234 5678 9012 3456").find());
        assertTrue(pattern.matcher("AB12 3456 7890 1234 5678").find());
        
        // 测试带连字符分隔的格式
        assertTrue(pattern.matcher("1234-5678-9012-3456").find());
        assertTrue(pattern.matcher("AB12-3456-7890-1234-5678").find());
        
        // 测试混合分隔符格式
        assertTrue(pattern.matcher("AB12 3456-7890 1234-5678").find());
        
        // 测试无效格式
        assertFalse(pattern.matcher("123456789012345").find()); // 太短
        assertFalse(pattern.matcher("1234567890123456789012345678901").find()); // 太长
    }
}