package com.sensitive.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 敏感数据识别应用程序主类
 */
@SpringBootApplication
public class SensitiveDataDetectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SensitiveDataDetectorApplication.class, args);
    }

}