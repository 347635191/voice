package com.yf.rj.disruptor.event;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public abstract class AbstractDisruptorEvent {
    protected Map<String, String> mdc;

    public abstract void clear();
}