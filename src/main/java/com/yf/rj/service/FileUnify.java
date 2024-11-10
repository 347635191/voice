package com.yf.rj.service;

import com.google.common.base.Preconditions;
import com.yf.rj.common.FileConstants;
import com.yf.rj.enums.FileTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface FileUnify<T> {
    Logger log = LoggerFactory.getLogger(FileUnify.class);

    default boolean handleRoot(File file) {
        return true;
    }

    default boolean handleSecond(File file) {
        return true;
    }

    default boolean handleThird(File file) {
        return true;
    }

    default void handleFourth(File file) {
    }

    /**
     * 通用遍历文件
     */
    default void commonHandle() {
        File rootDir = getRootDir();
        File[] files = rootDir.listFiles();
        Preconditions.checkNotNull(files);
        Arrays.stream(files)
                .filter(Objects::nonNull)
                .filter(this::handleRoot)
                .filter(File::isDirectory)
                .map(File::listFiles)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(this::handleSecond)
                .filter(File::isDirectory)
                .map(File::listFiles)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(this::handleThird)
                .filter(File::isDirectory)
                .map(File::listFiles)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .forEach(this::handleFourth);
    }

    /**
     * 通用处理最深层文件
     */
    default List<T> handleInner(FileTypeEnum typeEnum, Function<File, T> fun) {
        File rootDir = getRootDir();
        File[] files = rootDir.listFiles();
        Preconditions.checkNotNull(files);
        return Arrays.stream(files)
                .filter(Objects::nonNull)
                .filter(File::isDirectory)
                .map(File::listFiles)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(File::isDirectory)
                .map(File::listFiles)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(File::isDirectory)
                .map(File::listFiles)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(typeEnum::matches)
                .map(fun).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    static File getRootDir() {
        File rootDir = new File(FileConstants.ROOT_DIR);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            log.error("{}根路径不存在或不是文件夹", rootDir);
            throw new RuntimeException("服务器内部错误");
        }
        return rootDir;
    }
}