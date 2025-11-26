package com.sensitive.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.sensitive.data.model.SensitiveDataDetectionResult;
import com.sensitive.data.service.detector.SensitiveDataDetectorService;

/**
 * WebSocket控制器，用于实时敏感数据检测
 */
@Controller
public class WebSocketController {
    
    private final SensitiveDataDetectorService detectorService;
    
    @Autowired
    public WebSocketController(SensitiveDataDetectorService detectorService) {
        this.detectorService = detectorService;
    }
    
    /**
     * 处理实时敏感数据检测请求
     */
    @MessageMapping("/detect/realtime")
    @SendTo("/topic/detectionResults")
    public SensitiveDataDetectionResult detectRealtime(String text) {
        return detectorService.detectSensitiveDataRealtime(text);
    }
}