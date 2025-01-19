package com.yf.rj.metric;

import com.google.common.collect.Sets;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractMetricCollector {
    protected MeterRegistry meterRegistry;

    @Autowired
    public void setMeterRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    protected void createRealTimeGauge(String metric, AtomicLong state) {
        Gauge.builder(metric, state, value -> value.getAndSet(0))
                .description(metric).register(meterRegistry);
    }

    protected void createGauge(String metric, AtomicLong state) {
        Gauge.builder(metric, state, AtomicLong::get)
                .description(metric).register(meterRegistry);
    }

    protected void createRealTimeGauge(String metric, BlockingQueue<Long> state) {
        Gauge.builder(metric, state, queue -> {
            Set<Long> current = Sets.newHashSet();
            queue.drainTo(current);
            return current.isEmpty() ? 0 : Collections.max(current);
        }).description(metric).register(meterRegistry);
    }
}