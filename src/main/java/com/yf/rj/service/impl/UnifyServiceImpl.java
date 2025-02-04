package com.yf.rj.service.impl;

import com.yf.rj.dto.BaseException;
import com.yf.rj.enums.FileTypeEnum;
import com.yf.rj.enums.Mp3IndexEnum;
import com.yf.rj.enums.UnifyEnum;
import com.yf.rj.req.UnifyReq;
import com.yf.rj.service.FileUnify;
import com.yf.rj.service.UnifyService;
import com.yf.rj.util.FileUtil;
import com.yf.rj.util.WordUtil;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.*;

@Service
public class UnifyServiceImpl implements UnifyService, FileUnify<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(UnifyServiceImpl.class);
    private static final List<String> IGNORE_TRA = Arrays.asList("阪", "跤", "砂", "份", "俱", "陽", "葉", "倉");

    @Override
    public Object common(UnifyReq unifyReq) throws BaseException {
        switch (UnifyEnum.fromCode(unifyReq.getCode())) {
            case TITLE_AND_ALBUM:
                return setTitleAndAlbum();
            case GET_TRA:
                return getTradition();
            case REPLACE_KEYWORD:
                return replaceKeyWord(unifyReq);
            case DISCARD_SYMBOL:
                return discardSymbol();
        }
        return null;
    }

    private Integer discardSymbol() {
        List<Integer> modList = handleFourth(FileTypeEnum.LRC, lrcFile -> {
            try {
                return FileUtil.discardSymbol(lrcFile);
            } catch (BaseException e) {
                throw new RuntimeException(e);
            }
        });
        return modList.stream().reduce(Integer::sum).orElse(0);
    }

    /**
     * 统一替换关键字
     */
    private Integer replaceKeyWord(UnifyReq unifyReq) {
        List<Integer> modList = handleFourth(FileTypeEnum.LRC, lrcFile -> {
            try {
                return FileUtil.replaceKeyWord(lrcFile, unifyReq.getOldWord(), unifyReq.getNewWord(), unifyReq.getReplaceType());
            } catch (BaseException e) {
                throw new RuntimeException(e);
            }
        });
        return modList.stream().reduce(Integer::sum).orElse(0);
    }

    /**
     * 统一获取繁体字
     */
    private Set<String> getTradition() {
        Set<String> result = new HashSet<>();
        handleFourth(FileTypeEnum.LRC, lrcFile -> {
            String path = lrcFile.getAbsolutePath();
            String simPath = WordUtil.simplify(path);
            addRecord(path, simPath, result);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(lrcFile.toPath())))) {
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    String simStr = WordUtil.simplify(str);
                    addRecord(str, simStr, result);
                }
            } catch (Exception e) {
                throw new RuntimeException("读取文件失败：" + lrcFile.getAbsolutePath());
            }
            return null;
        });
        return result;
    }

    /**
     * 统一设置标题和唱片集
     */
    private String setTitleAndAlbum() {
        handleThird(FileTypeEnum.DIR, dir -> {
            String albumName = dir.getName();
            Optional.ofNullable(dir.listFiles())
                    .map(Arrays::asList)
                    .orElse(new ArrayList<>())
                    .stream()
                    .filter(FileTypeEnum.MP3::match)
                    .forEach(file -> {
                        Map<Mp3IndexEnum, String> map = new HashMap<>();
                        map.put(Mp3IndexEnum.TITLE, file.getName());
                        map.put(Mp3IndexEnum.ALBUM, albumName);
                        FileUtil.writeAttr(file, map);
                    });
            return null;
        });
        return "统一设置标题和唱片集成功";
    }

    private static void addRecord(String str, String simStr, Set<String> result) {
        char[] oldCharArr = str.toCharArray();
        char[] newCharArr = simStr.toCharArray();
        for (int i = 0; i < oldCharArr.length; i++) {
            char oldChar = oldCharArr[i];
            char newChar = newCharArr[i];
            if (oldChar != newChar && !IGNORE_TRA.contains(String.valueOf(oldChar))) {
                result.add(oldChar + "，" + newChar);
            }
        }
    }
}