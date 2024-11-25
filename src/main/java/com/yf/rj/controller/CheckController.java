package com.yf.rj.controller;

import com.yf.rj.dto.BaseException;
import com.yf.rj.req.CheckReq;
import com.yf.rj.service.CheckService;
import com.yf.rj.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/check")
public class CheckController {
    @Resource
    private CheckService checkService;

    /**
     * 通用检查
     *
     * @return 通用返回
     */
    @GetMapping("/common")
    public Result common(@Valid CheckReq checkReq) throws BaseException {
        return Result.success(checkService.common(checkReq));
    }
}