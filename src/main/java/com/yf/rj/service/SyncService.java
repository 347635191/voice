package com.yf.rj.service;

import com.yf.rj.dto.BaseException;

public interface SyncService {
    /**
     * 全量同步
     */
    void syncFull() throws BaseException;
}