package com.sensitive.data.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sensitive.data.model.SensitiveDataDetectionResult;
import com.sensitive.data.model.SensitiveDataType;
import com.sensitive.data.service.detector.SensitiveDataDetectorService;
import com.sensitive.data.service.processor.SensitiveDataProcessorService;

/**
 * 敏感数据检测REST API控制器
 */
@RestController
@RequestMapping("/api/detect")
public class SensitiveDataController {
    
    private final SensitiveDataDetectorService detectorService;
    private final SensitiveDataProcessorService processorService;
    
    @Autowired
    public SensitiveDataController(SensitiveDataDetectorService detectorService, 
                                  SensitiveDataProcessorService processorService) {
        this.detectorService = detectorService;
        this.processorService = processorService;
    }
    
    /**
     * 检测文本中的敏感数据
     */
    @PostMapping("/text")
    public SensitiveDataDetectionResult detectText(@RequestBody String text) {
        return detectorService.detectSensitiveData(text);
    }
    
    /**
     * 获取支持的敏感数据类型
     */
    @GetMapping("/types")
    public List<SensitiveDataTypeInfo> getSensitiveDataTypes() {
        return Arrays.stream(SensitiveDataType.values())
                .map(type -> new SensitiveDataTypeInfo(
                        type.getCode(), 
                        type.getName(), 
                        type.getRiskLevel().getName(),
                        type.getRiskLevel().getColor()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取特定类型数据的处理建议
     */
    @GetMapping("/suggestions/{type}")
    public String getProcessingSuggestion(@PathVariable String type) {
        SensitiveDataType dataType = SensitiveDataType.getByCode(type);
        if (dataType == null) {
            return "不支持的敏感数据类型";
        }
        return processorService.getProcessingSuggestion(dataType);
    }
    
    /**
     * 敏感数据类型信息（用于前端展示）
     */
    public static class SensitiveDataTypeInfo {
        private String code;
        private String name;
        private String riskLevel;
        private String riskColor;
        
        public SensitiveDataTypeInfo(String code, String name, String riskLevel, String riskColor) {
            this.code = code;
            this.name = name;
            this.riskLevel = riskLevel;
            this.riskColor = riskColor;
        }
        
        // Getters
        public String getCode() {
            return code;
        }
        
        public String getName() {
            return name;
        }
        
        public String getRiskLevel() {
            return riskLevel;
        }
        
        public String getRiskColor() {
            return riskColor;
        }
    }
}