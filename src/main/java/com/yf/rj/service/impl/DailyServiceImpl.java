package com.yf.rj.service.impl;

import com.yf.rj.common.FileConstants;
import com.yf.rj.common.LrcConstants;
import com.yf.rj.dto.BaseException;
import com.yf.rj.enums.DailyEnum;
import com.yf.rj.enums.FileTypeEnum;
import com.yf.rj.req.DailyReq;
import com.yf.rj.service.DailyService;
import com.yf.rj.util.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DailyServiceImpl implements DailyService {
    private static final Logger LOG = LoggerFactory.getLogger(DailyServiceImpl.class);

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
            case BATCH_PIC_NAME:
                return batchPicName(dailyReq);
            case ADD_PREFIX:
                return addPrefix(dailyReq);
            case SEQ_OFFSET:
                return seqOffset(dailyReq);
            case REPLACE_TITLE:
                return replaceTitle(dailyReq);
        }
        return null;
    }

    /**
     * 替换标题关键字
     */
    private String replaceTitle(DailyReq dailyReq) throws BaseException {
        File[] files = FileUtil.preCheck(ClipboardUtil.read());
        String oldWord = dailyReq.getOldWord();
        String newWord = dailyReq.getNewWord();
        int count = 0;
        for (File file : files) {
            if (FileTypeEnum.LRC.match(file) || FileTypeEnum.MP3.match(file)) {
                if (StringUtils.isBlank(oldWord)) {
                    throw new BaseException("必须输入旧值");
                }
                String name = file.getName();
                String newFileName = name.replaceAll(oldWord, newWord);
                if (name.equals(newFileName)) {
                    continue;
                }
                String newFilePath = RegexUtil.last(file.getAbsolutePath(), "\\\\") + newFileName;
                boolean changed = file.renameTo(new File(newFilePath));
                if (!changed) {
                    LOG.warn("{}修改文件名失败", file.getAbsolutePath());
                    continue;
                }
                count++;
            }
        }
        return "替换标题关键字：" + count;
    }

    /**
     * 序号偏移量
     */
    private String seqOffset(DailyReq dailyReq) throws BaseException {
        File[] files = FileUtil.preCheck(ClipboardUtil.read());
        Integer start = dailyReq.getStart();
        Integer end = dailyReq.getEnd();
        Integer offset = dailyReq.getSkip();
        if (ObjectUtils.anyNull(start, end, offset)) {
            throw new BaseException("开始和结束序号和偏移量必输");
        }
        if (end < start) {
            throw new BaseException("结束序号不能小于开始序号");
        }
        Arrays.stream(files).filter(file -> FileUtil.endAnyWithIgnoreCase(file, FileTypeEnum.LRC.getSuffix(), FileTypeEnum.MP3.getSuffix()))
                .forEach(file -> {
                    Integer seq = FileUtil.getSeq(file);
                    if (seq < start || seq > end) {
                        return;
                    }
                    seq += offset;
                    String newFileName = seq + "." + file.getName().split("\\.", 2)[1];
                    String newPath = RegexUtil.last(file.getAbsolutePath(), "\\\\") + newFileName;
                    boolean isRenamed = file.renameTo(new File(newPath));
                    if (!isRenamed) {
                        LOG.warn("{}序号偏移量修改失败", file.getAbsolutePath());
                    }
                });
        return "序号偏移量修改成功";
    }

    /**
     * 统一添加前缀
     */
    private String addPrefix(DailyReq dailyReq) throws BaseException {
        File[] files = FileUtil.preCheck(ClipboardUtil.read());
        Integer start = dailyReq.getStart();
        Integer end = dailyReq.getEnd();
        String prefix = dailyReq.getNewWord();
        if (ObjectUtils.anyNull(start, end)) {
            throw new BaseException("开始和结束序号必输");
        }
        if (end < start) {
            throw new BaseException("结束序号不能小于开始序号");
        }
        Arrays.stream(files).filter(file -> FileUtil.endAnyWithIgnoreCase(file, FileTypeEnum.LRC.getSuffix(), FileTypeEnum.MP3.getSuffix()))
                .forEach(file -> {
                    Integer seq = FileUtil.getSeq(file);
                    if (seq < start || seq > end) {
                        return;
                    }
                    if (file.getName().contains(prefix)) {
                        return;
                    }
                    if (FileUtil.endAnyWithIgnoreCase(file, FileTypeEnum.MP3.getSuffix())
                            && RegexUtil.invalidMp3Name(file.getName())) {
                        LOG.info("{}标题不合法", file.getName());
                        return;
                    }
                    String word = FileUtil.getCleanName(file.getName());
                    //无需加空格隔开的
                    List<String> noSpace = Collections.singletonList("【H】");
                    String delimiter = noSpace.contains(prefix) ? StringUtils.EMPTY : StringUtils.SPACE;
                    String newFileName = file.getName().replace(word, prefix + delimiter + word);
                    String newPath = RegexUtil.last(file.getAbsolutePath(), "\\\\") + newFileName;
                    if (!file.renameTo(new File(newPath))) {
                        LOG.warn("{}修改文件名失败", file.getAbsolutePath());
                    }
                });
        return "统一添加前缀成功";
    }

    /**
     * 批量处理图片名称
     */
    private String batchPicName(DailyReq dailyReq) throws BaseException {
        File[] files = FileUtil.preCheck(ClipboardUtil.read());
        Integer skip = dailyReq.getSkip();
        if (skip == null) {
            throw new BaseException("skip必输");
        }
        AtomicInteger seqGen = new AtomicInteger(skip);
        Arrays.stream(files).filter(file -> FileUtil.endAnyWithIgnoreCase(file, FileTypeEnum.PNG.getSuffix(), FileTypeEnum.JPG.getSuffix()))
                .sorted(Comparator.comparing(File::getName))
                .forEach(file -> {
                    int seq = seqGen.getAndIncrement();
                    String seqStr = seq == 0 ? StringUtils.EMPTY : String.valueOf(seq);
                    String[] split = file.getName().split("\\.");
                    String newFileName = FileConstants.PIC_PREFIX + seqStr + "." + split[1];
                    try {
                        Files.copy(file.toPath(), Paths.get(RegexUtil.last(file.getAbsolutePath(), "\\\\") + newFileName));
                    } catch (IOException e) {
                        LOG.warn("{}批量处理图片名称失败", file.getAbsolutePath());
                        throw new RuntimeException(e);
                    }
                });
        return "批量处理图片名称成功";
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
                count += FileUtil.replaceKeyWord(file, dailyReq.getOldWord(), dailyReq.getNewWord(), dailyReq.getReplaceType());
            }
        }
        return "替换关键字成功：" + count;
    }

    /**
     * 获取声优
     */
    private String getArtist() throws BaseException {
        String words = ClipboardUtil.read();
        String artist = DlSiteDomUtil.formatArtist(words);
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
        String comment = DlSiteDomUtil.formatComment(words);
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
            if (FileUtil.endAnyWithIgnoreCase(file, FileTypeEnum.LRC.getSuffix(), FileTypeEnum.VTT.getSuffix())) {
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
                String newFileName;
                if (FileUtil.endAnyWithIgnoreCase(file, FileTypeEnum.LRC.getSuffix())) {
                    newFileName = WordUtil.simplify(FileUtil.simplifyName(file, FileTypeEnum.LRC));
                } else {
                    newFileName = WordUtil.simplify(file.getName());
                }
                try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(
                        Paths.get(FileConstants.DESKTOP_DIR + "\\" + newFileName))))) {
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
                String absolutePath = prePath + FileTypeEnum.MP3.getSuffix();
                String fileName = FileUtil.getFileName(absolutePath);
                //检查文件名正确性
                if (RegexUtil.invalidMp3Name(fileName)) {
                    throw new BaseException("文件名非法：" + absolutePath);
                }
                word = FileUtil.getCleanNameFromPath(absolutePath);
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
                FileUtil.delete(file);
                String newFileName = FileUtil.simplifyName(file, FileTypeEnum.LRC);
                if (needSimplify) {
                    newFileName = WordUtil.simplify(newFileName);
                }
                try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(
                        Paths.get(FileConstants.DESKTOP_DIR + "\\" + newFileName))))) {
                    bufferedWriter.write(result.substring(0, result.length() - 1));
                    bufferedWriter.flush();
                } catch (IOException e) {
                    throw new BaseException("创建文件失败：" + file.getName());
                }
            }
        }
        return "格式化LRC成功";
    }

    private boolean checkNeedSimplify(DailyReq dailyReq) throws BaseException {
        Boolean needSimplify = dailyReq.getNeedSimplify();
        if (needSimplify == null) {
            throw new BaseException("是否需要转简体必输");
        }
        return needSimplify;
    }
}