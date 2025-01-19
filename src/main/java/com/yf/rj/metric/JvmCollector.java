package com.yf.rj.metric;

import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class JvmCollector extends AbstractMetricCollector implements InitializingBean {
    private final Set<MeterBinder> meterBinderSet = new HashSet<>();

    private void initMetrics() {
        //JVM指标
        meterBinderSet.add(new JvmGcMetrics());
        meterBinderSet.add(new JvmThreadMetrics());
        meterBinderSet.add(new JvmMemoryMetrics());

        //系统指标
        meterBinderSet.add(new ProcessorMetrics());
        meterBinderSet.add(new UptimeMetrics());
    }

    @Override
    public void afterPropertiesSet() {
        initMetrics();
        meterBinderSet.forEach(binder -> binder.bindTo(meterRegistry));
    }
}