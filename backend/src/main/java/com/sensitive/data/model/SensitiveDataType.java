package com.sensitive.data.model;

/**
 * 敏感数据类型枚举
 */
public enum SensitiveDataType {
    // 个人身份信息
    ID_CARD("身份证号", "idCard", RiskLevel.HIGH),
    PASSPORT("护照号", "passport", RiskLevel.HIGH),
    DRIVER_LICENSE("驾照号", "driverLicense", RiskLevel.MEDIUM),
    
    // 联系方式
    PHONE_NUMBER("手机号", "phoneNumber", RiskLevel.MEDIUM),
    EMAIL("邮箱", "email", RiskLevel.LOW),
    ADDRESS("地址", "address", RiskLevel.LOW),
    
    // 金融信息
    BANK_CARD("银行卡号", "bankCard", RiskLevel.HIGH),
    CREDIT_CARD("信用卡号", "creditCard", RiskLevel.HIGH),
    BANK_ACCOUNT("银行账号", "bankAccount", RiskLevel.HIGH),
    
    // 账号密码
    USERNAME("用户名", "username", RiskLevel.MEDIUM),
    PASSWORD("密码", "password", RiskLevel.HIGH),
    
    // 企业敏感信息
    TRADE_SECRET("商业机密", "tradeSecret", RiskLevel.HIGH),
    INTERNAL_CODE("内部代码", "internalCode", RiskLevel.MEDIUM),
    
    // 其他敏感信息
    POLITICAL("政治敏感", "political", RiskLevel.HIGH),
    PORNOGRAPHY("色情内容", "pornography", RiskLevel.HIGH),
    VIOLENCE("暴力内容", "violence", RiskLevel.HIGH),
    COPYRIGHT("版权内容", "copyright", RiskLevel.MEDIUM),
    HEALTH_RECORD("健康记录", "healthRecord", RiskLevel.HIGH),
    RELIGION("宗教信仰", "religion", RiskLevel.MEDIUM);
    
    private final String name;
    private final String code;
    private final RiskLevel riskLevel;
    
    SensitiveDataType(String name, String code, RiskLevel riskLevel) {
        this.name = name;
        this.code = code;
        this.riskLevel = riskLevel;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCode() {
        return code;
    }
    
    public RiskLevel getRiskLevel() {
        return riskLevel;
    }
    
    /**
     * 根据代码获取敏感数据类型
     */
    public static SensitiveDataType getByCode(String code) {
        for (SensitiveDataType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}