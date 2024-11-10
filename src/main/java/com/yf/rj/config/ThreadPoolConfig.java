package com.yf.rj.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {
    private static final Logger log = LoggerFactory.getLogger(ThreadPoolConfig.class);

    @Bean
    public ThreadPoolExecutor ioExecutor() {
        int count = Runtime.getRuntime().availableProcessors();
        log.info("虚拟机可用处理器个数：{}", count);
        return new ThreadPoolExecutor(count, 2 * count, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(3000),
                new ThreadFactoryBuilder().setNameFormat("mp3-sync-full-%d").build(), new ThreadPoolExecutor.CallerRunsPolicy());
    }
}