package com.yf.rj;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


/**
 * 〇
 * Ci-en特典
 * 早期特典
 * 特典
 * EX
 * Live2D
 * 追加音轨
 * SP
 */
//@MapperScan(basePackages = {"com.yf.rj.mapper"})
@SpringBootApplication
@ConfigurationPropertiesScan
public class VoiceRunner {
    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(VoiceRunner.class);
        //解决不能使用剪切板的问题
        builder.headless(false).run(args);
    }
}