package com.yf.rj.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class Mp3T {
    private Long id;
    /**
     * RJ号
     */
    private String rj;
    /**
     * RJ名
     */
    private String rjName;
    /**
     * 序号
     */
    private Integer seq;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 标题
     */
    private String title;
    /**
     * 唱片集
     */
    private String album;
    /**
     * 分类id
     */
    private Integer categoryId;
    /**
     * 比特率
     */
    private Integer bitRate;
    /**
     * 声优名
     */
    private String artist;
    /**
     * 年
     */
    private String year;
    /**
     * 汉化组名称
     */
    private String genre;
    /**
     * 音声标签
     */
    private String comment;
    /**
     * 系列名
     */
    private String series;
    /**
     * 社团名
     */
    private String composer;
    private String createTime;
    private String updateTime;

    public String getUniKey() {
        return rj + seq;
    }

    public String getUnionName() {
        return rj + StringUtils.SPACE + fileName;
    }
}