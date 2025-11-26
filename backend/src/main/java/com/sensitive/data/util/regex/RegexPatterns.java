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
        // 身份证号：支持15位和18位，最后一位可能是X
        // 15位：[1-9]\d{5}\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}
        // 18位：[1-9]\d{5}(19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]
        PATTERNS.put(SensitiveDataType.ID_CARD, 
                Pattern.compile("\\b(([1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx])|([1-9]\\d{5}\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}))\\b"));
        
        // 护照号：字母开头，后跟8位数字
        PATTERNS.put(SensitiveDataType.PASSPORT, 
                Pattern.compile("\\b[A-Za-z][0-9]{8}\\b"));
        
        // 手机号：11位数字，以1开头，第二位为3-9
        PATTERNS.put(SensitiveDataType.PHONE_NUMBER, 
                Pattern.compile("\\b1[3-9]\\d{9}\\b"));
        
        // 邮箱：标准邮箱格式，支持更严格的域名验证
        PATTERNS.put(SensitiveDataType.EMAIL, 
                Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b", Pattern.CASE_INSENSITIVE));
        
        // 银行卡号：16-19位数字，支持更多卡种前缀
        PATTERNS.put(SensitiveDataType.BANK_CARD, 
                Pattern.compile("\\b[1-9]\\d{15,18}\\b"));
        
        // 信用卡号：16位数字，以4、5、6开头
        PATTERNS.put(SensitiveDataType.CREDIT_CARD, 
                Pattern.compile("\\b[4-6]\\d{15}\\b"));
        
        // 密码：包含至少6位字符，至少一个字母和一个数字
        PATTERNS.put(SensitiveDataType.PASSWORD, 
                Pattern.compile("(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,}\\b"));
        
        // 驾照号：17位数字和字母组合
        PATTERNS.put(SensitiveDataType.DRIVER_LICENSE, 
                Pattern.compile("\\b[A-Za-z0-9]{17}\\b"));
        
        // 银行账号：支持16-25位数字
        PATTERNS.put(SensitiveDataType.BANK_ACCOUNT, 
                Pattern.compile("\\b[1-9]\\d{15,24}\\b"));
        
        // 用户名：字母开头，允许字母、数字、下划线，长度4-20
        PATTERNS.put(SensitiveDataType.USERNAME, 
                Pattern.compile("\\b[A-Za-z][A-Za-z0-9_]{3,19}\\b"));
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