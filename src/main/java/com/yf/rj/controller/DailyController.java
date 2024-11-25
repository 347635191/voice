package com.yf.rj.controller;

import com.yf.rj.dto.BaseException;
import com.yf.rj.req.DailyReq;
import com.yf.rj.service.DailyService;
import com.yf.rj.vo.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/daily")
public class DailyController {
    @Resource
    private DailyService dailyService;

    /**
     * 通用处理
     *
     * @return 通用返回
     */
    @GetMapping("/common")
    public Result common(@Valid DailyReq dailyReq) throws BaseException {
        return Result.success(dailyService.common(dailyReq));
    }
}