package com.yf.rj.controller;

import com.yf.rj.dto.BaseException;
import com.yf.rj.service.SyncService;
import com.yf.rj.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sync")
public class SyncController {
    @Resource
    private SyncService syncService;

    /**
     * 全量同步入库
     *
     * @return 通用返回
     */
    @PostMapping("/full")
    public Result syncFull() throws BaseException {
        syncService.syncFull();
        return Result.success("全量同步完成");
    }
}