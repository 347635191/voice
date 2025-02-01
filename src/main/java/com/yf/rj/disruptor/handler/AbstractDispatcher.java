package com.yf.rj.disruptor.handler;

import java.util.List;

/**
 * 事件总线
 */
public abstract class AbstractDispatcher<T> {
    public abstract void sync(List<T> events);
}