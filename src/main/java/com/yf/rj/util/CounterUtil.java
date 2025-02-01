package com.yf.rj.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CounterUtil {
    private static final AtomicLong SYNC_TOTAL = new AtomicLong();

    public static Long addSyncTotal(long count) {
        return SYNC_TOTAL.addAndGet(count);
    }

    public static AtomicLong getSyncTotal() {
        return SYNC_TOTAL;
    }

    public static void cleanSyncTotal() {
        SYNC_TOTAL.set(0);
    }
}