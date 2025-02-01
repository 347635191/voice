package com.yf.rj.disruptor.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor
@Getter
@Setter
public class Mp3Event extends AbstractDisruptorEvent {
    /**
     * 起始时间
     */
    private Long startTime;
    /**
     * 起始时间
     */
    private Long endTime;
    /**
     * 事件主题
     */
    private String topic;
    /**
     * 事件数据
     */
    private Object content;
    /**
     * 事件发布回调
     */
    private CompletableFuture<?> callback;
    private Long sequence;

    @Override
    public void clear() {
        mdc = null;
        startTime = null;
        endTime = null;
        topic = null;
        content = null;
        callback = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mp3Event mp3Event = (Mp3Event) o;
        return Objects.equals(topic, mp3Event.topic) && Objects.equals(content, mp3Event.content) && Objects.equals(callback, mp3Event.callback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, content, callback);
    }
}