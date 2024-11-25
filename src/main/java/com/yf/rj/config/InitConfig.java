package com.yf.rj.config;

import com.yf.rj.cache.CategoryDb;
import com.yf.rj.cache.Mp3Db;
import com.yf.rj.entity.CategoryT;
import com.yf.rj.entity.Mp3T;
import com.yf.rj.mapper.CategoryMapper;
import com.yf.rj.mapper.Mp3Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Configuration
public class InitConfig {
    private static final Logger LOG = LoggerFactory.getLogger(InitConfig.class);

    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private Mp3Mapper mp3Mapper;

    @PostConstruct
    public void init() {
        loadCategory();
        loadMp3();
    }

    private void loadCategory() {
        List<CategoryT> categoryList = categoryMapper.queryAll();
        CategoryDb.upset(categoryList, false);
        LOG.info("全部分类加载成功：{}", categoryList.size());
    }

    private void loadMp3() {
        List<Mp3T> mp3List = mp3Mapper.queryAll();
        Mp3Db.upset(mp3List, false);
        LOG.info("全部音声加载成功：{}", mp3List.size());
    }
}