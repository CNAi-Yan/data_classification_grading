package com.sensitive.data;

import com.sensitive.data.model.SensitiveDataType;
import com.sensitive.data.util.regex.RegexPatterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple regex test class
 */
public class RegexTest {
    
    public static void main(String[] args) {
        // Test ID_CARD regex
        testRegex(SensitiveDataType.ID_CARD, "110101199001011234");
        
        // Test PHONE_NUMBER regex
        testRegex(SensitiveDataType.PHONE_NUMBER, "13812345678");
        
        // Test EMAIL regex
        testRegex(SensitiveDataType.EMAIL, "zhangsan@example.com");
        
        // Test USERNAME regex
        testRegex(SensitiveDataType.USERNAME, "example");
        
        // Test PASSWORD regex
        testRegex(SensitiveDataType.PASSWORD, "His password is Abc123456");
        testRegex(SensitiveDataType.PASSWORD, "他的密码是Abc123456");
    }
    
    private static void testRegex(SensitiveDataType type, String text) {
        System.out.println("=== Testing " + type + " regex ===");
        System.out.println("Test text: " + text);
        
        Pattern pattern = RegexPatterns.getPattern(type);
        System.out.println("Regex pattern: " + pattern.pattern());
        
        Matcher matcher = pattern.matcher(text);
        boolean found = matcher.find();
        System.out.println("Match result: " + found);
        
        if (found) {
            System.out.println("Matched content: " + matcher.group());
            if (matcher.groupCount() > 0) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    System.out.println("Group " + i + ": " + matcher.group(i));
                }
            }
        }
        
        System.out.println();
    }
}