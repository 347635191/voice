package com.yf.rj.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

//@RefreshScope
@ConfigurationProperties(prefix = "asmrone")
@Getter
@Setter
public class AsmrOneProperties {
    private String wordInfoUrl;
    private String searchUrl;
}