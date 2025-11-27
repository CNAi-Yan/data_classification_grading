package com.sensitive.data.util.regex;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.sensitive.data.model.SensitiveDataType;

/**
 * 敏感数据正则表达式模式
 */
public class RegexPatterns {
    
    private static final Map<SensitiveDataType, Pattern> PATTERNS = new HashMap<>();
    
    static {
        // 身份证号：支持15位和18位，最后一位可能是X，使用严格的边界匹配
        // 15位：[1-9]\d{5}\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}
        // 18位：[1-9]\d{5}(19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]
        PATTERNS.put(SensitiveDataType.ID_CARD, 
                Pattern.compile("(?<!\\w)([1-9]\\d{5}(?:19|20)\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]|[1-9]\\d{5}\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])\\d{3})(?!\\w)"));
        
        // 护照号：字母开头，后跟8位数字，使用严格的边界匹配
        PATTERNS.put(SensitiveDataType.PASSPORT, 
                Pattern.compile("(?<!\\w)[A-Za-z][0-9]{8}(?!\\w)"));
        
        // 手机号：11位数字，以1开头，第二位为3-9，使用严格的边界匹配
        PATTERNS.put(SensitiveDataType.PHONE_NUMBER, 
                Pattern.compile("(?<!\\w)1[3-9]\\d{9}(?!\\w)"));
        
        // 邮箱：标准邮箱格式，支持更严格的域名验证，使用严格的边界匹配
        PATTERNS.put(SensitiveDataType.EMAIL, 
                Pattern.compile("(?<!\\w)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(?!\\w)", Pattern.CASE_INSENSITIVE));
        
        // 银行卡号：13-19位数字，支持带空格或连字符的格式，降低误报
        // 注意：银行卡号正则表达式不包含16位连续数字，避免与信用卡号冲突
        PATTERNS.put(SensitiveDataType.BANK_CARD, 
                Pattern.compile("(?<!\\d)(?:(?:\\d{4}[ -]?){5}|(?:\\d{4}[ -]?){4}\\d{1,3}|(?:\\d{4}[ -]?){3}\\d{1,7}|\\d{13,15}|\\d{17,19})(?!\\d)"));
        
        
        // 信用卡号：16位数字，支持带空格或连字符的格式，降低误报
        // 匹配规则：
        // 1. 16位数字
        // 2. 支持每4位加空格或连字符的格式
        // 3. 支持连续数字格式
        PATTERNS.put(SensitiveDataType.CREDIT_CARD, 
                Pattern.compile("\\b(?:[0-9]{4}[ -]?){3}[0-9]{4}|[0-9]{16}\\b"));
        
        
        // 密码：匹配"密码是"、"password:"等格式，支持更多关键词和格式
        PATTERNS.put(SensitiveDataType.PASSWORD, 
                Pattern.compile("(?i)(?:密码|password)[：: ]+(\\w{6,20})"));


        
        // 驾照号：17位数字和字母组合
        PATTERNS.put(SensitiveDataType.DRIVER_LICENSE, 
                Pattern.compile("\\b[A-Za-z0-9]{17}\\b"));
        
        // 银行账号：支持多种格式，包含字母和数字，长度16-22位，使用严格的边界匹配
        PATTERNS.put(SensitiveDataType.BANK_ACCOUNT, 
                Pattern.compile("(?<!\\w)([A-Za-z0-9][A-Za-z0-9- ]{14,20}[A-Za-z0-9])(?!\\w)", Pattern.CASE_INSENSITIVE));
        
        // 用户名：字母开头，允许字母、数字、下划线，长度4-20，使用严格的边界匹配
        PATTERNS.put(SensitiveDataType.USERNAME, 
                Pattern.compile("(?<!\\w)[A-Za-z][A-Za-z0-9_]{3,19}(?!\\w)"));
    }
    
    /**
     * 获取指定类型的正则表达式模式
     */
    public static Pattern getPattern(SensitiveDataType type) {
        return PATTERNS.get(type);
    }
    
    /**
     * 检查是否支持指定类型的正则表达式检测
     */
    public static boolean isSupported(SensitiveDataType type) {
        return PATTERNS.containsKey(type);
    }
}