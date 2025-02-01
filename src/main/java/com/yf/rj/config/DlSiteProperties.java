package com.yf.rj.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

//@RefreshScope
@ConfigurationProperties(prefix = "dlsite")
@Getter
@Setter
public class DlSiteProperties {
    private String htmlUrl;
    private String imgMain;
    private String imgSmp;
    private String img;
}