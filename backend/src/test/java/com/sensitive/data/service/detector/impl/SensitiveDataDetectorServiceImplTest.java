package com.sensitive.data.service.detector.impl;

import com.sensitive.data.model.SensitiveDataDetectionResult;
import com.sensitive.data.model.SensitiveDataItem;
import com.sensitive.data.model.SensitiveDataType;
import com.sensitive.data.service.processor.impl.SensitiveDataProcessorServiceImpl;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * 敏感数据检测器测试类
 */
public class SensitiveDataDetectorServiceImplTest {

    private final SensitiveDataDetectorServiceImpl detectorService = 
            new SensitiveDataDetectorServiceImpl(new SensitiveDataProcessorServiceImpl());

    @Test
    public void testBankCardDetection() {
        // 综合测试银行卡号检测功能
        
        // 测试1：有效银行卡号检测
        String validCardsText = "我的银行卡号是4111111111111111，信用卡号是5555555555554444。";
        SensitiveDataDetectionResult validResult = detectorService.detectSensitiveData(validCardsText);
        List<SensitiveDataItem> validItems = validResult.detectedItems();
        
        // 检查是否包含银行卡号和信用卡号
        boolean hasBankCard = validItems.stream().anyMatch(item -> item.type() == SensitiveDataType.BANK_CARD);
        boolean hasCreditCard = validItems.stream().anyMatch(item -> item.type() == SensitiveDataType.CREDIT_CARD);
        assertTrue(hasBankCard);
        assertTrue(hasCreditCard);
        
        // 测试2：无效银行卡号检测
        String invalidCardsText = "无效的银行卡号：6222021234567890124，这个也无效：4111111111111112。";
        SensitiveDataDetectionResult invalidResult = detectorService.detectSensitiveData(invalidCardsText);
        List<SensitiveDataItem> invalidItems = invalidResult.detectedItems();
        
        // 应该没有检测到银行卡号
        boolean hasInvalidBankCard = invalidItems.stream().anyMatch(item -> 
            item.type() == SensitiveDataType.BANK_CARD || item.type() == SensitiveDataType.CREDIT_CARD);
        assertFalse(hasInvalidBankCard);
        
        // 测试3：非银行卡号不会被误判
        String nonCardText = "电话号码：13800138000，身份证号：110101199001011237，普通数字：12345678901234567890。";
        SensitiveDataDetectionResult nonCardResult = detectorService.detectSensitiveData(nonCardText);
        List<SensitiveDataItem> nonCardItems = nonCardResult.detectedItems();
        
        // 应该检测到身份证号，但不应该检测到普通数字作为银行卡号
        long bankCardCount = nonCardItems.stream().filter(item -> 
            item.type() == SensitiveDataType.BANK_CARD || item.type() == SensitiveDataType.CREDIT_CARD).count();
        long idCardCount = nonCardItems.stream().filter(item -> 
            item.type() == SensitiveDataType.ID_CARD).count();
        assertEquals(0, bankCardCount); // 不应该检测到银行卡号
        assertEquals(1, idCardCount); // 应该检测到身份证号
    }

    @Test
    public void testBankCardDetectionWithRealTimeDetection() {
        // 测试实时检测功能
        String text = "实时检测：4111111111111111，无效卡号：6222021234567890124。";
        
        SensitiveDataDetectionResult result = detectorService.detectSensitiveDataRealtime(text);
        List<SensitiveDataItem> items = result.detectedItems();
        
        // 应该只检测到有效的银行卡号
        long bankCardCount = items.stream().filter(item -> item.type() == SensitiveDataType.BANK_CARD).count();
        assertEquals(1, bankCardCount);
        
        // 检查检测到的卡号是否正确
        if (bankCardCount > 0) {
            SensitiveDataItem bankCardItem = items.stream()
                .filter(item -> item.type() == SensitiveDataType.BANK_CARD)
                .findFirst().get();
            assertEquals("4111111111111111", bankCardItem.content());
        }
    }
}
