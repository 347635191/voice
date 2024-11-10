package com.yf.rj.entity;

import lombok.Data;

@Data
public class CategoryT {
    private Integer id;
    private String oneCategory;
    private String twoCategory;
    private String createTime;
    private String updateTime;

    public String getUniKey() {
        return oneCategory + "\\" + twoCategory;
    }
}