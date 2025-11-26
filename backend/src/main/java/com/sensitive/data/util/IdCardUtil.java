package com.sensitive.data.util;

/**
 * 身份证号工具类，用于验证身份证号的有效性
 */
public class IdCardUtil {
    
    /**
     * 验证身份证号的有效性，包括格式和校验码
     * @param idCard 身份证号
     * @return true表示身份证号有效，false表示无效
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return false;
        }
        
        // 去除空格
        idCard = idCard.trim();
        
        // 检查长度
        if (idCard.length() != 15 && idCard.length() != 18) {
            return false;
        }
        
        // 15位身份证号不需要校验码验证，直接返回true
        if (idCard.length() == 15) {
            return true;
        }
        
        // 18位身份证号需要校验码验证
        return validateChecksum(idCard);
    }
    
    /**
     * 验证18位身份证号的校验码
     * @param idCard 18位身份证号
     * @return true表示校验码有效，false表示无效
     */
    private static boolean validateChecksum(String idCard) {
        try {
            // 身份证号前17位
            String prefix = idCard.substring(0, 17);
            // 校验码
            char checksum = idCard.charAt(17);
            
            // 权重因子
            int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
            // 余数对应的校验码：0→1, 1→0, 2→X, 3→9, 4→8, 5→7, 6→6, 7→5, 8→4, 9→3, 10→2
            char[] checksumValues = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
            
            // 计算校验码
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                sum += (prefix.charAt(i) - '0') * weights[i];
            }
            
            // 计算校验码索引
            int checksumIndex = sum % 11;
            // 计算期望的校验码
            char expectedChecksum = checksumValues[checksumIndex];
            
            // 比较校验码
            return Character.toUpperCase(checksum) == expectedChecksum;
        } catch (Exception e) {
            // 如果发生异常，返回false
            return false;
        }
    }
}