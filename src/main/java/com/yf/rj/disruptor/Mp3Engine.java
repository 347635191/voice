package com.yf.rj.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.yf.rj.disruptor.event.Mp3Event;
import com.yf.rj.disruptor.handler.AbstractDispatcher;
import com.yf.rj.disruptor.handler.AbstractMicroBatchConsumer;
import com.yf.rj.disruptor.handler.DisruptorExceptionHandler;
import com.yf.rj.metric.DisruptorCollector;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Mp3Engine implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(Mp3Engine.class);
    private static final Disruptor<Mp3Event> DISRUPTOR;
    private static final String NAME = "MP3";
    private final Mp3Consumer mp3Consumer;

    static {
        DISRUPTOR = new Disruptor<>(Mp3Event::new, 1 << 12, DaemonThreadFactory.INSTANCE
                , ProducerType.MULTI, new YieldingWaitStrategy());
        //默认异常处理
        DISRUPTOR.setDefaultExceptionHandler(new DisruptorExceptionHandler<>(NAME));
        LOG.info("[{}]disruptor created.", NAME);
    }

    @Override
    public void afterPropertiesSet() {
        //消费者
        DISRUPTOR.handleEventsWith(mp3Consumer);
        DISRUPTOR.start();
        LOG.info("[{}]disruptor started.", NAME);
    }

    @Component
    public static class EventPublisher {
        public void publish(Mp3Event event) {
            Map<String, String> mdc = MDC.getCopyOfContextMap();
            RingBuffer<Mp3Event> ringBuffer = DISRUPTOR.getRingBuffer();
            ringBuffer.publishEvent((e, sequence, ars) -> {
                e.setMdc(mdc);
                e.setStartTime(event.getStartTime());
                e.setEndTime(event.getEndTime());
                e.setTopic(event.getTopic());
                e.setCallback(event.getCallback());
                e.setContent(event.getContent());
                e.setSequence(sequence);
                // 每隔128次采样一次
                if (((sequence & 127L) == 0)) {
                    long consumerSequence = ringBuffer.getMinimumGatingSequence();
                    DisruptorCollector.collectOverstock(sequence - consumerSequence);
                }
                // 生产者延时指标
                long end = System.currentTimeMillis();
                DisruptorCollector.collectProducerDelay(end - event.getStartTime());
            });
        }
    }

    @Component
    static class Mp3Consumer extends AbstractMicroBatchConsumer<Mp3Event> {
        /**
         * 事件分发
         */
        private final AbstractDispatcher<Mp3Event> abstractDispatcher;

        public Mp3Consumer(AbstractDispatcher<Mp3Event> abstractDispatcher) {
            super(false, NAME);
            this.abstractDispatcher = abstractDispatcher;
        }

        @Override
        protected void consume(List<Mp3Event> events) {
            abstractDispatcher.sync(events);
        }
    }
}