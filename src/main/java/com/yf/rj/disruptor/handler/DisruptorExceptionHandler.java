package com.yf.rj.disruptor.handler;

import com.lmax.disruptor.ExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class DisruptorExceptionHandler<T> implements ExceptionHandler<T> {
    private static final Logger LOG = LoggerFactory.getLogger(DisruptorExceptionHandler.class);
    private final String name;

    @Override
    public void handleEventException(Throwable ex, long sequence, T event) {
        LOG.error("[{}]disruptor consuming failed, event:{}", name, event, ex);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        LOG.error("[{}]disruptor start failed", name, ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        LOG.error("[{}]disruptor shutdown failed", name, ex);
    }
}