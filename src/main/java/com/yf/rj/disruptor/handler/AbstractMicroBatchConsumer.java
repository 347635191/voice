package com.yf.rj.disruptor.handler;

import com.google.common.collect.Lists;
import com.lmax.disruptor.EventHandler;
import com.yf.rj.disruptor.event.AbstractDisruptorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class AbstractMicroBatchConsumer<T extends AbstractDisruptorEvent> implements EventHandler<T> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractMicroBatchConsumer.class);
    /**
     * 是否对同批次事件进行去重
     */
    protected final boolean distinct;
    /**
     * 名称
     */
    protected final String name;
    /**
     * 任务队列
     */
    protected final Collection<T> workQueue;

    protected AbstractMicroBatchConsumer(boolean distinct, String name) {
        this.distinct = distinct;
        this.name = name;
        this.workQueue = distinct ? new LinkedHashSet<>() : new ArrayList<>();
    }

    @Override
    public void onEvent(T event, long sequence, boolean endOfBatch) {
        MDC.setContextMap(event.getMdc());
        //移除旧任务
        if (distinct) {
            workQueue.remove(event);
        }
        workQueue.add(event);
        if (endOfBatch) {
            //批量消费
            batchProcess();
            //垃圾回收
            workQueue.forEach(AbstractDisruptorEvent::clear);
            //清空队列
            workQueue.clear();
        }
        MDC.clear();
    }

    /**
     * 批量消费
     */
    private void batchProcess() {
        List<T> events = Lists.newArrayList(workQueue);
        try {
            consume(events);
        } catch (Exception e) {
            LOG.error("[{}]disruptor consuming failed, event:{}", name, events, e);
        } finally {
            // 垃圾回收
            workQueue.forEach(AbstractDisruptorEvent::clear);
            events.clear();
        }
    }

    protected abstract void consume(List<T> events);
}