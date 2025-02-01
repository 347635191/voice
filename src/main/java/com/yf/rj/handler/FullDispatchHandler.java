package com.yf.rj.handler;

import com.yf.rj.cache.CategoryDb;
import com.yf.rj.cache.Mp3Db;
import com.yf.rj.common.Topic;
import com.yf.rj.disruptor.Mp3Engine;
import com.yf.rj.disruptor.event.Mp3Event;
import com.yf.rj.entity.CategoryT;
import com.yf.rj.entity.Mp3T;
import com.yf.rj.enums.FileTypeEnum;
import com.yf.rj.mapper.CategoryMapper;
import com.yf.rj.mapper.Mp3Mapper;
import com.yf.rj.service.FileUnify;
import com.yf.rj.util.CounterUtil;
import com.yf.rj.util.DateUtil;
import com.yf.rj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Component
public class FullDispatchHandler implements FileUnify<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(FullDispatchHandler.class);

    @Resource
    private Mp3Mapper mp3Mapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private ThreadPoolTaskExecutor ioExecutor;
    @Resource
    private Mp3Engine.EventPublisher eventPublisher;

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
            CompletableFuture.runAsync(() -> {
                Mp3Event event = new Mp3Event();
                event.setStartTime(System.currentTimeMillis());
                event.setTopic(Topic.SYNC_ALL);
                event.setContent(buildMp3T(file));
                eventPublisher.publish(event);
            }, ioExecutor).exceptionally(e -> {
                LOG.info("ioExecutor failed：{}", file.getAbsolutePath(), e);
                return null;
            });
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
        //分类id
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
        CounterUtil.cleanSyncTotal();
    }
}