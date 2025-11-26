package com.sensitive.data.util;

/**
 * Luhn算法工具类，用于校验银行卡号、信用卡号等标识号的有效性
 */
public class LuhnUtil {
    
    /**
     * 使用Luhn算法校验卡号的有效性
     * 
     * @param cardNumber 卡号，支持带空格或连字符格式
     * @return true表示卡号有效，false表示无效
     */
    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }
        
        // 移除空格和连字符
        String cleanedNumber = cardNumber.replaceAll("[- ]", "");
        
        // 检查是否只包含数字且长度在13-19位之间（银行卡号标准长度）
        if (!cleanedNumber.matches("\\d{13,19}")) {
            return false;
        }
        

        
        int sum = 0;
        boolean isEven = false;
        
        // 从右到左遍历数字
        for (int i = cleanedNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cleanedNumber.charAt(i));
            
            if (isEven) {
                digit *= 2;
                // 如果乘积大于9，减去9
                if (digit > 9) {
                    digit -= 9;
                }
            }
            
            sum += digit;
            isEven = !isEven;
        }
        
        // 如果总和能被10整除，则卡号有效
        return sum % 10 == 0;
    }
}