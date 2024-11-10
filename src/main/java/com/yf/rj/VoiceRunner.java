package com.yf.rj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

//@MapperScan(basePackages = {"com.yf.rj.mapper"})
@SpringBootApplication
@ConfigurationPropertiesScan
public class VoiceRunner {
    public static void main(String[] args) {
        SpringApplication.run(VoiceRunner.class, args);
    }
}