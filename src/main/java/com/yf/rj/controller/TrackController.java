package com.yf.rj.controller;

import com.yf.rj.dto.BaseException;
import com.yf.rj.req.TrackReq;
import com.yf.rj.service.TrackService;
import com.yf.rj.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/track")
public class TrackController {
    @Resource
    private TrackService trackService;

    /**
     * 爬虫
     *
     * @return 通用返回
     */
    @GetMapping("/common")
    public Result common(@Valid TrackReq trackReq) throws BaseException {
        return Result.success(trackService.common(trackReq));
    }
}