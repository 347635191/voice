package com.yf.rj.service.impl;

import com.yf.rj.common.FileConstants;
import com.yf.rj.common.LrcConstants;
import com.yf.rj.common.SymbolConstants;
import com.yf.rj.dto.BaseException;
import com.yf.rj.enums.DailyEnum;
import com.yf.rj.enums.FileTypeEnum;
import com.yf.rj.req.DailyReq;
import com.yf.rj.service.DailyService;
import com.yf.rj.util.ClipboardUtil;
import com.yf.rj.util.FileUtil;
import com.yf.rj.util.RegexUtil;
import com.yf.rj.util.WordUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DailyServiceImpl implements DailyService {
    @Override
    public String common(DailyReq dailyReq) throws BaseException {
        switch (DailyEnum.fromCode(dailyReq.getCode())) {
            case ADD_TO_CHINESE_LRC:
            case ADD_ONE_LINE_LRC:
                return addLrc(dailyReq);
            case VTT_TO_LRC:
                return vttToLrc(dailyReq);
            case TRA_TO_SIMP:
                return traToSim();
            case FORMAT_LRC:
                return formatLrc(dailyReq);
            case GET_COMMENT:
                return getComment();
            case GET_ARTIST:
                return getArtist();
            case REPLACE_KEYWORD:
                return replaceKeyWord(dailyReq);
            case DISCARD_SYMBOL:
                return discardSymbol();
        }
        return null;
    }

    /**
     * 去特殊符号
     */
    private String discardSymbol() throws BaseException {
        File[] files = FileUtil.preCheck(FileConstants.DESKTOP_DIR);
        //修改文件的数量
        int modCount = 0;
        for (File file : files) {
            if (FileTypeEnum.LRC.match(file)) {
                modCount += FileUtil.discardSymbol(file);
            }
        }
        return "去除特殊符号成功：" + modCount;
    }

    /**
     * 替换关键字
     */
    private String replaceKeyWord(DailyReq dailyReq) throws BaseException {
        File[] files = FileUtil.preCheck(FileConstants.DESKTOP_DIR);
        int count = 0;
        for (File file : files) {
            if (FileTypeEnum.LRC.match(file)) {
                count += FileUtil.replaceKeyWord(file, dailyReq.getOldWord(), dailyReq.getNewWord());
            }
        }
        return "替换关键字成功：" + count;
    }


    /**
     * 获取声优
     */
    private String getArtist() throws BaseException {
        String words = ClipboardUtil.read();
        words = words.replaceAll("(琴音有波\\(紅月ことね\\))|(紅月ことね)", "琴音有波");
        words = words.replaceAll("(乙倉ゅい（乙倉由依）)|(乙倉ゅい\\(乙倉由依\\))", "乙倉ゅい");
        String artist = Arrays.stream(words.split(SymbolConstants.ARTIST_DELIMITER))
                .map(StringUtils::trim).sorted()
                .collect(Collectors.joining(SymbolConstants.COMMENT_DELIMITER));
        if (StringUtils.isBlank(artist)) {
            throw new BaseException("获取声优为空");
        }
        ClipboardUtil.write(artist);
        return "获取声优成功";
    }


    /**
     * 获取标签
     */
    private String getComment() throws BaseException {
        String words = ClipboardUtil.read();
        words = words.replaceAll("大量汁/液", "大量汁液");
        String comment = Arrays.stream(words.split(StringUtils.SPACE)).map(StringUtils::trim)
                .map(word -> {
                    word = word.replaceAll("[（(]", SymbolConstants.COMMENT_DELIMITER);
                    word = word.replaceAll("[）)]", "");
                    return word.split(SymbolConstants.COMMENT_DELIMITER);
                }).flatMap(Arrays::stream).map(StringUtils::trim).sorted()
                .collect(Collectors.joining(SymbolConstants.COMMENT_DELIMITER));
        if (StringUtils.isBlank(comment)) {
            throw new BaseException("获取标签为空");
        }
        ClipboardUtil.write(comment);
        return "获取标签成功";
    }

    /**
     * 繁体转简体
     */
    private String traToSim() throws BaseException {
        File[] files = FileUtil.preCheck(FileConstants.DESKTOP_DIR);
        for (File file : files) {
            if (FileUtil.endAnyWithIgnoreCase(file, FileTypeEnum.LRC.getSuffix())) {
                StringBuilder result = new StringBuilder();
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())))) {
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        String simplify = WordUtil.simplify(str);
                        result.append(simplify).append('\n');
                    }
                } catch (IOException e) {
                    throw new BaseException("读取文件失败：" + file.getName());
                }
                FileUtil.delete(file);
                try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(
                        Paths.get(FileConstants.DESKTOP_DIR + "\\" + WordUtil.simplify(FileUtil.simplifyName(file, FileTypeEnum.LRC))))))) {
                    bufferedWriter.write(result.substring(0, result.length() - 1));
                    bufferedWriter.flush();
                } catch (IOException e) {
                    throw new BaseException("创建文件失败：" + file.getName());
                }
            }
        }
        return "繁体转简体成功";
    }

    /**
     * VTT转LRC(兼容srt)
     */
    private String vttToLrc(DailyReq dailyReq) throws BaseException {
        boolean needSimplify = checkNeedSimplify(dailyReq);
        File[] files = FileUtil.preCheck(FileConstants.DESKTOP_DIR);
        int count = 0;
        for (File file : files) {
            if (FileUtil.endAnyWithIgnoreCase(file, FileTypeEnum.VTT.getSuffix(),
                    FileTypeEnum.TXT.getSuffix(), FileTypeEnum.SRT.getSuffix())) {
                StringBuilder result = new StringBuilder();
                //上一行读到的是否为时间
                boolean timeLine = false;
                //上一次读取的是否为文字
                boolean preWord = false;
                String startTimeStr = StringUtils.EMPTY;
                String endTimeStr = StringUtils.EMPTY;
                int index = 0;
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())))) {
                    String str;
                    while ((str = bufferedReader.readLine()) != null) {
                        str = str.trim();
                        if (needSimplify) {
                            str = WordUtil.simplify(str);
                        }
                        if (str.contains("-->")) {
                            if (index++ != 0) {
                                result.append(endTimeStr).append(" \n");
                            }
                            if (StringUtils.endsWithIgnoreCase(file.getName(), FileTypeEnum.SRT.getSuffix())) {
                                str = str.replaceAll(",", ".");
                            }
                            String[] timeArr = str.split("-->");
                            startTimeStr = WordUtil.getTime(timeArr[0].trim());
                            endTimeStr = WordUtil.getTime(timeArr[1].trim());
                            timeLine = true;
                            preWord = false;
                        } else {
                            if (StringUtils.isBlank(str)) {
                                if (timeLine && !preWord) {
                                    throw new BaseException(startTimeStr + "时间后字幕为空");
                                }
                                timeLine = false;
                                preWord = false;
                            }
                            if (timeLine) {
                                if (preWord) {
                                    result.append(endTimeStr).append(" \n");
                                }
                                result.append(startTimeStr).append(str).append("\n");
                                preWord = true;
                            }
                        }
                    }
                    result.append(endTimeStr).append(" ");
                } catch (IOException e) {
                    throw new BaseException("读取文件失败：" + file.getName());
                }
                FileUtil.delete(file);
                String newFileName = FileUtil.simplifyName(file, FileTypeEnum.LRC);
                if (needSimplify) {
                    newFileName = WordUtil.simplify(newFileName);
                }
                try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(
                        Paths.get(FileConstants.DESKTOP_DIR + "\\" + newFileName))))) {
                    bufferedWriter.write(result.toString());
                    bufferedWriter.flush();
                } catch (IOException e) {
                    throw new BaseException("创建文件失败：" + file.getName());
                }
                count++;
            }
        }
        return "VTT转LRC成功：" + count;
    }

    /**
     * 添加待汉化字幕
     */
    public String addLrc(DailyReq dailyReq) throws BaseException {
        File[] files = FileUtil.preCheck(ClipboardUtil.read());
        List<String> mp3List = new ArrayList<>();
        List<String> lrcList = new ArrayList<>();
        for (File file : files) {
            String absolutePath = file.getAbsolutePath();
            if (FileTypeEnum.MP3.match(file)) {
                mp3List.add(absolutePath.replaceAll(FileConstants.MP3_SUFFIX, ""));
            } else if (FileTypeEnum.LRC.match(file)) {
                lrcList.add(absolutePath.replaceAll(FileConstants.LRC_SUFFIX, ""));
            }
        }
        mp3List.removeAll(lrcList);
        for (String prePath : mp3List) {
            String word;
            if (DailyEnum.ADD_TO_CHINESE_LRC.getCode().equals(dailyReq.getCode())) {
                word = LrcConstants.UN_CHINESE;
            } else {
                word = RegexUtil.findFirst("(?<=\\d+\\.).*$", prePath.replace(LrcConstants.H_LABEL, ""));
            }
            FileUtil.write(prePath + FileConstants.LRC_SUFFIX,
                    MessageFormat.format(LrcConstants.ADD_LRC_WORD, word));
        }
        return "添加字幕成功：" + mp3List.size();
    }

    /**
     * 格式化lrc
     */
    private String formatLrc(DailyReq dailyReq) throws BaseException {
        boolean needSimplify = checkNeedSimplify(dailyReq);
        Integer skip = dailyReq.getSkip();
        if (Objects.isNull(skip)) {
            throw new BaseException("请输入跳过行数");
        }
        File[] files = FileUtil.preCheck(FileConstants.DESKTOP_DIR);
        for (File file : files) {
            if (FileUtil.endAnyWithIgnoreCase(file, FileTypeEnum.LRC.getSuffix())) {
                StringBuilder result = new StringBuilder();
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())))) {
                    String str;
                    int index = 0;
                    //上一行读取的时间
                    String lastTime = null;
                    //上一行读取的字幕
                    String lastWord = null;
                    while ((str = bufferedReader.readLine()) != null) {
                        if (index++ < skip) {
                            System.out.println(str);
                            continue;
                        }
                        if (index == skip + 1) {
                            if (str.charAt(0) == '\uFEFF') {
                                str = str.substring(1);
                            }
                        }
                        if (StringUtils.isBlank(str)) {
                            continue;
                        }
                        str = str.trim().replaceFirst("100]", "10]");
                        RegexUtil.checkLrcLine(str, file.getName());
                        if (needSimplify) {
                            str = WordUtil.simplify(str);
                        }
                        String[] split = str.split("]", 2);
                        String time = split[0] + "]";
                        String word = split[1].trim();
                        if (index == skip + 1 && StringUtils.isBlank(word)) {
                            throw new BaseException("第一行字幕不能为空：" + file.getName());
                        }
                        if (StringUtils.isNotBlank(word)) {
                            //如果不是第一行字幕。需要填充上一行的空行
                            if (index != skip + 1) {
                                if (StringUtils.isBlank(lastWord)) {
                                    //取lrc原有的空行
                                    result.append(lastTime).append(" \n");
                                } else {
                                    //取当前字幕行时间
                                    result.append(time).append(" \n");
                                }
                            }
                            result.append(time).append(word).append("\n");
                        }
                        lastTime = time;
                        lastWord = word;
                    }
                    if (StringUtils.isBlank(lastWord)) {
                        //取lrc原有的空行
                        result.append(lastTime).append(" \n");
                    } else {
                        //取自定义的结尾时间
                        result.append("[99:99.99]").append(" \n");
                    }
                } catch (IOException e) {
                    throw new BaseException("读取文件失败：" + file.getName());
                }
//              FileUtil.delete(file);
                String newFileName = FileUtil.simplifyName(file, FileTypeEnum.LRC);
                if (needSimplify) {
                    newFileName = WordUtil.simplify(newFileName);
                }
                try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(
                        Paths.get(FileConstants.DESKTOP_OUT_DIR + "\\" + newFileName))))) {
                    bufferedWriter.write(result.substring(0, result.length() - 1));
                    bufferedWriter.flush();
                } catch (IOException e) {
                    throw new BaseException("创建文件失败：" + file.getName());
                }
            }
        }
        return "繁体转简体成功";
    }

    private boolean checkNeedSimplify(DailyReq dailyReq) throws BaseException {
        Boolean needSimplify = dailyReq.getNeedSimplify();
        if (needSimplify == null) {
            throw new BaseException("是否需要转简体必输");
        }
        return needSimplify;
    }
}