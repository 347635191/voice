package com.yf.rj.disruptor.handler;

import com.yf.rj.disruptor.event.Mp3Event;
import com.yf.rj.disruptor.flow.AbstractFlow;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Mp3Dispatcher extends AbstractDispatcher<Mp3Event> {
    private static final Logger LOG = LoggerFactory.getLogger(Mp3Dispatcher.class);
    private final Map<String, AbstractFlow> nodes;

    @Override
    public void sync(List<Mp3Event> events) {
        Map<String, List<Mp3Event>> topicMap = events.stream().collect(Collectors.groupingBy(Mp3Event::getTopic));
        topicMap.forEach((topic, eventList) -> Optional.ofNullable(nodes.get(topic))
                .ifPresent(node -> node.handle(eventList)));
    }
}