package com.yf.rj.service.impl;

import com.yf.rj.dto.BaseException;
import com.yf.rj.handler.FullDispatchHandler;
import com.yf.rj.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SyncServiceImpl implements SyncService {
    private static final Logger LOG = LoggerFactory.getLogger(SyncServiceImpl.class);

    @Resource
    private FullDispatchHandler fullDispatchHandler;

    @Override
//    @Transactional(rollbackFor = Exception.class)
    public void syncFull() throws BaseException {
        long start = System.currentTimeMillis();
        fullDispatchHandler.clearData();
        fullDispatchHandler.commonHandle();
        LOG.info("全量同步耗时：{}毫秒", System.currentTimeMillis() - start);
    }
}