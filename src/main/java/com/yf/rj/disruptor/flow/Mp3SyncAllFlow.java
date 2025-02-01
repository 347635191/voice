package com.yf.rj.disruptor.flow;

import com.yf.rj.cache.Mp3Db;
import com.yf.rj.common.Topic;
import com.yf.rj.disruptor.event.Mp3Event;
import com.yf.rj.entity.Mp3T;
import com.yf.rj.mapper.Mp3Mapper;
import com.yf.rj.metric.AllSyncCollector;
import com.yf.rj.util.CounterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component(Topic.SYNC_ALL)
public class Mp3SyncAllFlow extends AbstractFlow {
    private static final Logger LOG = LoggerFactory.getLogger(Mp3SyncAllFlow.class);

    @Resource
    private Mp3Mapper mp3Mapper;
    @Resource
    private ThreadPoolTaskExecutor dbExecutor;

    @Override
    public void handle(List<Mp3Event> events) {
        try {
            Long start = events.get(0).getStartTime();
            List<Mp3T> mp3List = events.stream().map(Mp3Event::getContent)
                    .map(content -> (Mp3T) content).collect(Collectors.toList());
            Mp3Db.upset(mp3List, false);
            //异步入库
            CompletableFuture.runAsync(() -> {
                mp3Mapper.batchInsert(mp3List);
                //入库总量
                CounterUtil.addSyncTotal(mp3List.size());
                //入库延时
                AllSyncCollector.collectDelay(System.currentTimeMillis() - start);
            }, dbExecutor).exceptionally(e -> {
                LOG.info("dbExecutor failed", e);
                return null;
            });
        } catch (Exception e) {
            LOG.error("全量mp3分批入库失败", e);
        }
    }
}