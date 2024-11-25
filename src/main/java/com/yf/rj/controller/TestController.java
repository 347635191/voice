package com.yf.rj.controller;

import com.yf.rj.cache.CategoryDb;
import com.yf.rj.cache.Mp3Db;
import com.yf.rj.handler.TrackHandler;
import com.yf.rj.vo.Result;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private TrackHandler trackHandler;

    @GetMapping("/pool")
    public Result pool() {
        LettuceClientConfiguration clientConfiguration = ((LettuceConnectionFactory) applicationContext.getBean(RedisConnectionFactory.class)).getClientConfiguration();
        return Result.success(clientConfiguration.getClass().getName());
    }

    @GetMapping("/categoryDbSize")
    public Result categoryDbSize() {
        return Result.success(CategoryDb.queryAll().size());
    }

    @GetMapping("/mp3DbSize")
    public Result mp3DbSize() {
        return Result.success(Mp3Db.queryAll().size());
    }

    @GetMapping("/mp3DbByRj")
    public Result mp3DbByRj(String rj) {
        return Result.success(Mp3Db.queryByRj(rj));
    }

    @GetMapping("/specialSymbol")
    public Result specialSymbol() {
        String str = "\u2764\ufe0f";
        System.out.println(str);
        return Result.success(str);
    }

    @GetMapping("/workInfo")
    public Result workInfo() {
        String workInfo = trackHandler.getWorkInfo("01246585");
        return Result.success(workInfo);
    }
}