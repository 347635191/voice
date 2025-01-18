package com.yf.rj.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtil {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FULL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static String formatDateTime(FileTime fileTime) {
        LocalDateTime localDateTime = fileTime
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return localDateTime.format(FULL_DATE_TIME_FORMATTER);
    }

    public static String getDateTimeNow() {
        return LocalDateTime.now().format(FULL_DATE_TIME_FORMATTER);
    }

    /**
     * 毫秒转分钟
     */
    public static String convert(Long milliSeconds) {
        long seconds = milliSeconds / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%d分%d秒", minutes, remainingSeconds);
    }
}