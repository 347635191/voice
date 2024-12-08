package com.yf.rj.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class StreamUtil {
    /**
     * 根据指定字段去重
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> keySet = ConcurrentHashMap.newKeySet();
        return bean -> keySet.add(keyExtractor.apply(bean));
    }
}