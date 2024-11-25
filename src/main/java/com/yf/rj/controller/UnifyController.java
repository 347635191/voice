package com.yf.rj.controller;

import com.yf.rj.dto.BaseException;
import com.yf.rj.req.UnifyReq;
import com.yf.rj.service.UnifyService;
import com.yf.rj.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/unify")
public class UnifyController {
    @Resource
    private UnifyService unifyService;

    /**
     * 统一处理
     *
     * @return 通用返回
     */
    @GetMapping("/common")
    public Result common(@Valid UnifyReq unifyReq) throws BaseException {
        return Result.success(unifyService.common(unifyReq));
    }
}