package com.yf.rj.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TraceUtil {
    public static final String KEY = "traceId";
    public static String create(){
        long trace = ThreadLocalRandom.current().nextLong(100000000000L, 999999999999L);
        return String.valueOf(trace);
    }
}