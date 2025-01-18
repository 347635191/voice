package com.yf.rj.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolConfig.class);

    @Bean
    public ThreadPoolExecutor ioExecutor() {
        int count = Runtime.getRuntime().availableProcessors();
        LOG.info("虚拟机可用处理器个数：{}", count);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(count, 2 * count
                , 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(3000)
                , new ThreadFactoryBuilder().setNameFormat("collect-mp3-attr-%d").build()
                , new ThreadPoolExecutor.CallerRunsPolicy());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownGracefully(executor, "收集音频属性线程池")));
        return executor;
    }

    @Bean
    public ScheduledThreadPoolExecutor scheduledExecutor() {
        ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("mp3-batch-insert-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownGracefully(scheduledExecutor, "音频批量入库线程池")));
        return scheduledExecutor;
    }

    private void shutdownGracefully(ExecutorService executor, String poolName) {
        LOG.info("[{}]优雅关闭线程池开始", poolName);
        //不会接收新任务，等待已经提交的任务执行完毕后再停止
        executor.shutdown();
        try {
            //超时等待线程池关闭
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                LOG.info("[{}]优雅关闭线程池超时，开始强制关闭", poolName);
                //中断正在执行任务的线程
                executor.shutdown();
            }
        } catch (InterruptedException e) {
            LOG.error("[{}]优雅关闭线线程池失败，重新强制关闭线程池", poolName);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        LOG.info("[{}]优雅关闭线程池成功", poolName);
    }
}