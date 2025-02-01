package com.yf.rj.metric;

import com.yf.rj.util.CounterUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class AllSyncCollector extends AbstractMetricCollector implements InitializingBean {
    private static final BlockingQueue<Long> CONSUME_DELAY = new LinkedBlockingDeque<>();

    public static void collectDelay(long delay) {
        CONSUME_DELAY.add(delay);
    }

    @Override
    public void afterPropertiesSet() {
        createGauge(Config.MP3_SYNC_TOTAL, CounterUtil.getSyncTotal());
        createRealTimeGauge(Config.MP3_CONSUME_DELAY, CONSUME_DELAY);
    }

    private static final class Config {
        /**
         * 入库总量
         */
        private static final String MP3_SYNC_TOTAL = "mp3_sync_total";
        /**
         * 入库延时
         */
        private static final String MP3_CONSUME_DELAY = "mp3_consume_delay";
    }
}