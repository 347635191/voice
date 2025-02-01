package com.yf.rj.trace;

import com.yf.rj.util.TraceUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskDecorator {
    private static final Logger LOG = LoggerFactory.getLogger(TaskDecorator.class);

    public static Runnable decorate(Runnable runnable) {
        Map<String, String> ctx = MDC.getCopyOfContextMap();
        Map<String, String> map = Optional.ofNullable(ctx).orElseGet(HashMap::new);
        map.putIfAbsent(TraceUtil.KEY, TraceUtil.create());
        return () -> {
            try {
                MDC.setContextMap(map);
                runnable.run();
            } catch (Exception e) {
                LOG.warn("{}任务执行失败", Thread.currentThread().getName(), e);
            } finally {
                MDC.clear();
            }
        };
    }
}