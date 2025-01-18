package com.yf.rj.service.impl;

import com.yf.rj.handler.FullDispatchHandler;
import com.yf.rj.service.SyncService;
import com.yf.rj.util.DateUtil;
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
    public void syncFull() {
        long start = System.currentTimeMillis();
        fullDispatchHandler.clearData();
        fullDispatchHandler.commonHandle();
        fullDispatchHandler.waitSyncFinished();
        LOG.info("全量同步耗时：{}", DateUtil.convert(System.currentTimeMillis() - start));
    }
}