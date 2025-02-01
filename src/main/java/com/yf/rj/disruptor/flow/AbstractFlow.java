package com.yf.rj.disruptor.flow;

import com.yf.rj.disruptor.event.Mp3Event;

import java.util.List;

public abstract class AbstractFlow {
    public abstract void handle(List<Mp3Event> event);
}