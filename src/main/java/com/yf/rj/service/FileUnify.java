package com.yf.rj.service;

import com.google.common.base.Preconditions;
import com.yf.rj.common.FileConstants;
import com.yf.rj.enums.FileTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface FileUnify<T> {
    Logger LOG = LoggerFactory.getLogger(FileUnify.class);

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

    default boolean handleRootDir(File dir) {
        return true;
    }

    default boolean handleSecondDir(File dir) {
        return true;
    }

    default boolean handleThirdDir(File dir) {
        return true;
    }

    default void handleFourthDir(File dir) {
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
     * 通用遍历文件夹
     */
    default void commonHandleDir() {
        File rootDir = getRootDir();
        Optional.of(rootDir)
                .filter(this::handleRootDir)
                .map(File::listFiles)
                .map(Arrays::asList)
                .orElse(new ArrayList<>())
                .stream()
                .filter(File::isDirectory)
                .filter(this::handleSecondDir)
                .map(File::listFiles)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(File::isDirectory)
                .filter(this::handleThirdDir)
                .map(File::listFiles)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(File::isDirectory)
                .forEach(this::handleFourthDir);
    }

    /**
     * 通用处理第四层文件
     */
    default List<T> handleFourth(FileTypeEnum typeEnum, Function<File, T> fun) {
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
                .filter(typeEnum::match)
                .map(fun).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 通用处理第三层文件
     */
    default List<T> handleThird(FileTypeEnum typeEnum, Function<File, T> fun) {
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
                .filter(typeEnum::match)
                .map(fun).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    static File getRootDir() {
        File rootDir = new File(FileConstants.ROOT_DIR);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            LOG.error("{}根路径不存在或不是文件夹", rootDir);
            throw new RuntimeException("服务器内部错误");
        }
        return rootDir;
    }
}