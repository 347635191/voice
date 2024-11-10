package com.yf.rj.mapper;

import com.yf.rj.entity.CategoryT;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryT> queryAll();

    int insert(CategoryT categoryT);

    int truncate();
}