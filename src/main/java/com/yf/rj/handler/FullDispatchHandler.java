package com.yf.rj.handler;

import com.yf.rj.cache.CategoryDb;
import com.yf.rj.cache.Mp3Db;
import com.yf.rj.entity.CategoryT;
import com.yf.rj.entity.Mp3T;
import com.yf.rj.enums.FileTypeEnum;
import com.yf.rj.mapper.CategoryMapper;
import com.yf.rj.mapper.Mp3Mapper;
import com.yf.rj.metric.AllSyncCollector;
import com.yf.rj.service.FileUnify;
import com.yf.rj.util.DateUtil;
import com.yf.rj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FullDispatchHandler implements FileUnify<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(FullDispatchHandler.class);
    private ArrayBlockingQueue<Mp3T> mp3SyncQueue;
    private static final AtomicLong TOTAL = new AtomicLong();
    private static final List<CompletableFuture<Void>> futureList = new ArrayList<>();

    @Value("${fullDispatch.totalQueueSize}")
    private int totalQueueSize;

    @Value("${fullDispatch.enabled}")
    private boolean enabled;

    @Resource
    private Mp3Mapper mp3Mapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private ThreadPoolExecutor ioExecutor;
    @Resource
    private ScheduledThreadPoolExecutor scheduledExecutor;

    @PostConstruct
    public void init() {
        mp3SyncQueue = new ArrayBlockingQueue<>(totalQueueSize);
        if (!enabled) {
            LOG.info("全量mp3同步分发队已关闭");
            return;
        }
        scheduledExecutor.scheduleAtFixedRate(new Mp3DispatchWork(), 0, 5, TimeUnit.SECONDS);
        LOG.info("全量mp3同步分发队列已开启，totalQueueSize：{}", totalQueueSize);
    }

    @Override
    public boolean handleSecond(File file) {
        if (FileTypeEnum.DIR.match(file)) {
            CategoryT categoryT = new CategoryT();
            categoryT.setOneCategory(FileUtil.getOneCategory(file));
            categoryT.setTwoCategory(FileUtil.getTwoCategory(file));
            categoryT.setCreateTime(DateUtil.getDateTimeNow());
            categoryT.setUpdateTime(categoryT.getCreateTime());
            categoryMapper.insert(categoryT);
            CategoryDb.upset(Collections.singletonList(categoryT), false);
        }
        return true;
    }

    @Override
    public void handleFourth(File file) {
        if (FileTypeEnum.MP3.match(file)) {
            //采集队列积压
            AllSyncCollector.collectOverstock(mp3SyncQueue.size());
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    Mp3T mp3T = buildMp3T(file);
                    mp3SyncQueue.put(mp3T);
                } catch (InterruptedException e) {
                    LOG.error("mp3入队失败，文件名：{}", file.getName());
                }
            }, ioExecutor).exceptionally(e -> {
                LOG.info("ioExecutor failed：{}", file.getAbsolutePath(), e);
                return null;
            });
            futureList.add(future);
        }
    }

    public class Mp3DispatchWork implements Runnable {
        @Override
        public void run() {
            List<Mp3T> collect = Stream.generate(mp3SyncQueue::poll).limit(500)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect)) {
                AllSyncCollector.clearDelay();
                return;
            }
            //此处一定要try-catch，否则异常会被吞掉
            try {
                //采集消费延时
                collect.stream().map(Mp3T::getStartMillis).map(start -> System.currentTimeMillis() - start)
                        .forEach(AllSyncCollector::collectDelay);
                mp3Mapper.batchInsert(collect);
                Mp3Db.upset(collect, false);
                int batchSize = collect.size();
                long totalSize = TOTAL.addAndGet(collect.size());
                LOG.info("全量mp3分批入库成功，本次条数：{}，总条数：{}", batchSize, totalSize);
            } catch (Exception e) {
                LOG.error("全量mp3分批入库失败", e);
            }
        }
    }

    private static Mp3T buildMp3T(File file) {
        Mp3T mp3T = new Mp3T();
        mp3T.setStartMillis(System.currentTimeMillis());
        //补充其他属性
        FileUtil.fixAttr(file, mp3T);
        //分类id
        fixCategoryId(file, mp3T);
        //RJ号
        mp3T.setRj(FileUtil.getRj(file));
        //RJ名
        mp3T.setRjName(FileUtil.getRjName(file));
        //序号
        mp3T.setSeq(FileUtil.getSeq(file));
        //文件名称
        mp3T.setFileName(file.getName());
        //创建时间
        mp3T.setCreateTime(FileUtil.getCreateTime(file));
        //更新时间
        mp3T.setUpdateTime(DateUtil.getDateTimeNow());
        return mp3T;
    }

    private static void fixCategoryId(File file, Mp3T mp3T) {
        String oneCategory = FileUtil.getOneCategory(file);
        String twoCategory = FileUtil.getTwoCategory(file, oneCategory);
        CategoryT categoryT = CategoryDb.queryByCategory(oneCategory, twoCategory);
        mp3T.setCategoryId(categoryT.getId());
    }

    /**
     * 全量同步前清空缓存和数据库，这样后面就不用单独处理删除的文件了
     */
    public void clearData() {
        CategoryDb.clear();
        Mp3Db.clear();
        categoryMapper.truncate();
        LOG.info("清空分类表");
        mp3Mapper.truncate();
        LOG.info("清空音频表");
        TOTAL.set(0);
        futureList.clear();
    }

    /**
     * 主线程等待异步落库完成
     */
    public void waitSyncFinished() {
        LOG.info("单次批量入库的异步任务数：{}", futureList.size());
        CompletableFuture<Void> finalFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        finalFuture.join();
    }

    public static AtomicLong getTotal() {
        return TOTAL;
    }
}