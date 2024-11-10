package com.yf.rj.controller;

import com.yf.rj.dto.BaseException;
import com.yf.rj.req.SearchReq;
import com.yf.rj.service.SearchService;
import com.yf.rj.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Resource
    private SearchService searchService;

    /**
     * 通用搜索mp3属性
     */
    @GetMapping("/common")
    public Result common(@Valid SearchReq searchReq) throws BaseException {
        return Result.success(searchService.common(searchReq));
    }
}