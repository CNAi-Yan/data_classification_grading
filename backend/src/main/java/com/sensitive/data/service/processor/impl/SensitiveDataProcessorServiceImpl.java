package com.sensitive.data.service.processor.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.sensitive.data.model.SensitiveDataType;
import com.sensitive.data.service.processor.SensitiveDataProcessorService;

/**
 * 敏感数据处理服务实现
 */
@Service
public class SensitiveDataProcessorServiceImpl implements SensitiveDataProcessorService {
    
    /**
     * 重复字符串指定次数，兼容早期Java版本
     */
    private String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    @Override
    public String getProcessingSuggestion(SensitiveDataType type) {
        switch (type) {
            case ID_CARD:
                return "建议使用掩码处理，保留前6位和后4位，中间用*代替";
            case PASSPORT:
                return "建议使用掩码处理，保留前2位和后2位，中间用*代替";
            case PHONE_NUMBER:
                return "建议使用掩码处理，保留前3位和后4位，中间用*代替";
            case EMAIL:
                return "建议使用掩码处理，保留域名和邮箱首字母，其余用*代替";
            case BANK_CARD:
                return "建议使用掩码处理，保留前4位和后4位，中间用*代替";
            case CREDIT_CARD:
                return "建议使用掩码处理，保留前4位和后4位，中间用*代替";
            case PASSWORD:
                return "建议使用加密存储，禁止明文传输和显示";
            case USERNAME:
                return "建议使用哈希处理后存储";
            case TRADE_SECRET:
                return "建议使用访问控制和加密存储";
            case INTERNAL_CODE:
                return "建议使用访问控制和版本管理";
            case POLITICAL:
                return "建议删除或替换为中性词汇";
            case PORNOGRAPHY:
                return "建议删除或替换为健康词汇";
            case VIOLENCE:
                return "建议删除或替换为文明词汇";
            case COPYRIGHT:
                return "建议获取授权或删除相关内容";
            case HEALTH_RECORD:
                return "建议使用加密存储和严格的访问控制";
            case RELIGION:
                return "建议避免使用极端表述，保持中立";
            default:
                return "建议根据数据敏感程度采取适当的保护措施";
        }
    }
    
    @Override
    public String maskSensitiveData(String data, SensitiveDataType type) {
        if (StringUtils.isBlank(data)) {
            return data;
        }
        
        switch (type) {
            case ID_CARD:
                return maskIdCard(data);
            case PASSPORT:
                return maskPassport(data);
            case PHONE_NUMBER:
                return maskPhoneNumber(data);
            case EMAIL:
                return maskEmail(data);
            case BANK_CARD:
            case CREDIT_CARD:
                return maskBankCard(data);
            case PASSWORD:
                return "********";
            case USERNAME:
                return maskUsername(data);
            default:
                return maskGeneral(data);
        }
    }
    
    /**
     * 掩码处理身份证号
     */
    private String maskIdCard(String idCard) {
        if (idCard.length() == 15) {
            return idCard.substring(0, 6) + "****" + idCard.substring(11);
        } else if (idCard.length() == 18) {
            return idCard.substring(0, 6) + "********" + idCard.substring(14);
        }
        return idCard;
    }
    
    /**
     * 掩码处理护照号
     */
    private String maskPassport(String passport) {
        if (passport.length() <= 4) {
            return passport;
        }
        return passport.substring(0, 2) + repeatString("*", passport.length() - 4) + passport.substring(passport.length() - 2);
    }
    
    /**
     * 掩码处理手机号
     */
    private String maskPhoneNumber(String phone) {
        if (phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
    
    /**
     * 掩码处理邮箱
     */
    private String maskEmail(String email) {
        int atIndex = email.lastIndexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (username.length() <= 2) {
            return username.charAt(0) + "*" + domain;
        }
        
        return username.charAt(0) + repeatString("*", username.length() - 1) + domain;
    }
    
    /**
     * 掩码处理银行卡号
     */
    private String maskBankCard(String cardNumber) {
        if (cardNumber.length() <= 8) {
            return cardNumber;
        }
        
        int length = cardNumber.length();
        return cardNumber.substring(0, 4) + repeatString("*", length - 8) + cardNumber.substring(length - 4);
    }
    
    /**
     * 掩码处理用户名
     */
    private String maskUsername(String username) {
        if (username.length() <= 2) {
            return username;
        }
        
        if (username.length() == 3) {
            return username.charAt(0) + "*" + username.charAt(2);
        }
        
        return username.charAt(0) + repeatString("*", username.length() - 2) + username.charAt(username.length() - 1);
    }
    
    /**
     * 通用掩码处理
     */
    private String maskGeneral(String data) {
        if (data.length() <= 3) {
            return data;
        }
        
        int maskLength = Math.min(data.length() - 2, 6);
        return data.charAt(0) + repeatString("*", maskLength) + data.substring(data.length() - 1);
    }
}