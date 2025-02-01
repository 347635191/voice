package com.yf.rj.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WordProperties {
    @Getter
    private static String[] hWords;
    @Getter
    private static String[] errorWords;
    @Getter
    private static String[] excludeWords;
    @Getter
    private static String[] trackExclude;
    @Getter
    private static String[] trackInclude;

    @Value("#{'${words.h}'.split(',')}")
    public void sethWords(String[] hWords) {
        WordProperties.hWords = hWords;
    }

    @Value("#{'${words.error}'.split(',')}")
    public void setErrorWords(String[] errorWords) {
        WordProperties.errorWords = errorWords;
    }

    @Value("#{'${words.exclude}'.split(',')}")
    public void setExcludeWords(String[] excludeWords) {
        WordProperties.excludeWords = excludeWords;
    }

    @Value("#{'${track.exclude}'.split(',')}")
    public void setTrackExclude(String[] trackExclude) {
        WordProperties.trackExclude = trackExclude;
    }

    @Value("#{'${track.include}'.split(',')}")
    public void setTrackInclude(String[] trackInclude) {
        WordProperties.trackInclude = trackInclude;
    }
}