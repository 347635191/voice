package com.yf.rj.metric;

import com.yf.rj.handler.FullDispatchHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class AllSyncCollector extends AbstractMetricCollector implements InitializingBean {
    private static final BlockingQueue<Long> OVERSTOCK_QUEUE = new LinkedBlockingDeque<>();
    private static final AtomicLong CONSUME_DELAY = new AtomicLong();

    public static void collectOverstock(long overstock) {
        OVERSTOCK_QUEUE.add(overstock);
    }

    public static void collectDelay(long delay) {
        CONSUME_DELAY.getAndSet(delay);
    }

    public static void clearDelay() {
        CONSUME_DELAY.getAndSet(0);
    }

    @Override
    public void afterPropertiesSet() {
        createRealTimeGauge(Config.MP3_SYNC_QUEUE_OVERSTOCK, OVERSTOCK_QUEUE);
        createGauge(Config.MP3_CONSUME_DELAY, CONSUME_DELAY);
        createGauge(Config.MP3_SYNC_COUNT, FullDispatchHandler.getTotal());
    }

    private static final class Config {
        private static final String MP3_SYNC_QUEUE_OVERSTOCK = "mp3_sync_queue_overstock";
        private static final String MP3_SYNC_COUNT = "mp3_sync_count";
        private static final String MP3_CONSUME_DELAY = "mp3_consume_delay";
    }
}