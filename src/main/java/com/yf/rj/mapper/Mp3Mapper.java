package com.yf.rj.mapper;

import com.yf.rj.entity.CategoryT;
import com.yf.rj.entity.Mp3T;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface Mp3Mapper {
    List<Mp3T> queryAll();

    int batchInsert(@Param("list") List<Mp3T> mp3TList);

    void truncate();
}