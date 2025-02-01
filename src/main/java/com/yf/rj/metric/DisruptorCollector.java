package com.yf.rj.metric;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class DisruptorCollector extends AbstractMetricCollector implements InitializingBean {
    private static final BlockingQueue<Long> OVERSTOCK_QUEUE = new LinkedBlockingDeque<>();
    private static final BlockingQueue<Long> PRODUCER_DELAY = new LinkedBlockingDeque<>();

    public static void collectOverstock(long overstock) {
        OVERSTOCK_QUEUE.add(overstock);
    }

    public static void collectProducerDelay(long delay) {
        PRODUCER_DELAY.add(delay);
    }

    @Override
    public void afterPropertiesSet() {
        createRealTimeGauge(Config.MP3_QUEUE_OVERSTOCK, OVERSTOCK_QUEUE);
        createRealTimeGauge(Config.MP3_PRODUCER_DELAY, PRODUCER_DELAY);
    }

    private static final class Config {
        /**
         * 队列积压
         */
        private static final String MP3_QUEUE_OVERSTOCK = "mp3_queue_overstock";
        /**
         * 生产者延时指标
         */
        private static final String MP3_PRODUCER_DELAY = "mp3_producer_delay";
    }
}