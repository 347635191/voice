package com.yf.rj.util;

import com.yf.rj.common.FileConstants;
import com.yf.rj.dto.BaseException;
import com.yf.rj.entity.Mp3T;
import com.yf.rj.enums.FileTypeEnum;
import com.yf.rj.enums.ReplaceTypeEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    public static Integer getBitRate(File file) {
        MP3File mp3File;
        try {
            mp3File = new MP3File(file);
            MP3AudioHeader header = mp3File.getMP3AudioHeader();
            return Integer.parseInt(header.getBitRate());
        } catch (Exception e) {
            LOG.error("获取比特率失败，文件名：{}", file.getName());
            return null;
        }
    }

    public static String getLength(File file) {
        MP3File mp3File;
        try {
            mp3File = new MP3File(file);
            MP3AudioHeader header = mp3File.getMP3AudioHeader();
            return header.getTrackLengthAsString();
        } catch (Exception e) {
            LOG.error("获取时常失败，文件名：{}", file.getName());
            return null;
        }
    }

    public static String getField(File file, FieldKey key) {
        MP3File mp3File;
        try {
            mp3File = new MP3File(file);
            AbstractID3v2Tag id3v2Tag = mp3File.getID3v2Tag();
            return id3v2Tag.getFirst(key);
        } catch (Exception e) {
            LOG.error("获取{}失败，文件名：{}", key.getClass().getName(), file.getName());
            return null;
        }
    }

    public static void fixAttr(File file, Mp3T mp3T) {
        MP3File mp3File;
        try {
            mp3File = new MP3File(file);
            MP3AudioHeader header = mp3File.getMP3AudioHeader();
            //比特率
            mp3T.setBitRate(Integer.parseInt(header.getBitRate()));

            AbstractID3v2Tag id3v2Tag = mp3File.getID3v2Tag();
            //标题
            String title = id3v2Tag.getFirst(FieldKey.TITLE);
            if (StringUtils.isNotBlank(title)) {
                mp3T.setTitle(title);
            }
            //唱片集
            String album = id3v2Tag.getFirst(FieldKey.ALBUM);
            if (StringUtils.isNotBlank(album)) {
                mp3T.setAlbum(album);
            }
            //声优
            String artist = id3v2Tag.getFirst(FieldKey.ARTIST);
            if (StringUtils.isNotBlank(artist)) {
                mp3T.setArtist(artist);
            }
            //年份
            String year = id3v2Tag.getFirst(FieldKey.YEAR);
            if (StringUtils.isNotBlank(year)) {
                mp3T.setYear(year);
            }
            //汉化组名称
            String genre = id3v2Tag.getFirst(FieldKey.GENRE);
            if (StringUtils.isNotBlank(genre)) {
                mp3T.setGenre(genre);
            }
            //标签
            String comment = id3v2Tag.getFirst(FieldKey.COMMENT);
            if (StringUtils.isNotBlank(comment)) {
                mp3T.setComment(comment);
            }
            //系列
            String series = id3v2Tag.getFirst(FieldKey.ALBUM_ARTIST);
            if (StringUtils.isNotBlank(series)) {
                mp3T.setSeries(series);
            }
            //社团
            String composer = id3v2Tag.getFirst(FieldKey.COMPOSER);
            if (StringUtils.isNotBlank(composer)) {
                mp3T.setComposer(composer);
            }
        } catch (Exception e) {
            LOG.error("获取属性失败，文件名：{}", file.getName());
        }
    }

    public static String getRj(File file) {
        return getRj(file.getAbsolutePath());
    }

    public static String getRj(String fileName) {
        return RegexUtil.findFirst("RJ\\d+", fileName);
    }

    public static Integer getSeq(File file) {
        String seqStr = RegexUtil.findFirst("^\\d+", file.getName());
        return Integer.parseInt(seqStr);
    }

    public static String getRjName(File file) {
        String regex = "RJ\\d+.*?(?=\\\\\\d+\\.)";
        return RegexUtil.findFirst(regex, file.getAbsolutePath());
    }

    public static String getOneCategory(File file) {
        String regex = "(?<=" + FileConstants.FIRST_DIR_NAME + "\\\\).*?(?=\\\\)";
        return RegexUtil.findFirst(regex, file.getAbsolutePath());
    }

    public static String getTwoCategory(File file) {
        String regex = "[^\\\\]+$";
        return RegexUtil.findFirst(regex, file.getAbsolutePath());
    }

    public static String getTwoCategory(File file, String oneCategory) {
        String regex = "(?<=" + oneCategory + "\\\\).*?(?=\\\\RJ\\d+)";
        return RegexUtil.findFirst(regex, file.getAbsolutePath());
    }

    public static String getCreateTime(File file) {
        BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(Paths.get(file.getAbsolutePath()), BasicFileAttributes.class);
            FileTime fileTime = attributes.creationTime();
            return DateUtil.formatDateTime(fileTime);
        } catch (IOException e) {
            LOG.error("获取文件创建时间失败，文件名：{}", file.getName());
            return null;
        }
    }

    public static void write(String path, String text) throws BaseException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(path))))) {
            bufferedWriter.write(text);
            bufferedWriter.flush();
        } catch (Exception e) {
            throw new BaseException("写入文件失败：" + path);
        }
    }

    public static File[] preCheck(String path) throws BaseException {
        if (StringUtils.isBlank(path)) {
            throw new BaseException("请指定文件夹");
        }
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new BaseException("文件夹不存在");
        }
        File[] files = dir.listFiles();
        if (files == null) {
            throw new BaseException("子文件为空");
        }
        return files;
    }

    public static boolean endAnyWithIgnoreCase(File file, String... suffixes) {
        String name = file.getName();
        for (String suffix : suffixes) {
            if (StringUtils.endsWithIgnoreCase(name, suffix)) {
                return true;
            }
        }
        return false;
    }

    public static String simplifyName(File file, FileTypeEnum typeEnum) {
        String name = file.getName();
        for (String suffix : FileTypeEnum.getSuffixes()) {
            name = name.replaceAll("(?i)" + suffix, "");
        }
        return name + typeEnum.getSuffix();
    }

    public static void delete(File file) throws BaseException {
        if (!file.delete()) {
            throw new BaseException("删除文件失败：" + file.getName());
        }
    }

    /**
     * 过滤lrc、mp3、ini
     */
    public static boolean matchPic(String name) {
        return Pattern.matches("^(?!.*(mp3|lrc|ini)$).*", name);
    }

    public static boolean matchMp3Lrc(String name) {
        return Pattern.matches(".*\\.(mp3|lrc)$", name);
    }

    public static boolean checkPicName(String fileName) {
        return Pattern.matches("main\\d{0,3}\\.(jpg|png)", fileName);
    }

    /**
     * lrc替换关键字
     */
    public static int replaceKeyWord(File file, String oldWord, String newWord, Integer replaceType) throws BaseException {
        if (StringUtils.isBlank(oldWord)) {
            throw new BaseException("必须输入旧值");
        }
        ReplaceTypeEnum replaceEnum = ReplaceTypeEnum.fromCode(replaceType);
        if (replaceEnum == null) {
            throw new BaseException("输入的替换类型不合法");
        }
        boolean needChange = false;
        String newFileName = FileUtil.simplifyName(file, FileTypeEnum.LRC);
        if (replaceEnum == ReplaceTypeEnum.TITLE || replaceEnum == ReplaceTypeEnum.ALL) {
            newFileName = newFileName.replaceAll(oldWord, newWord);
        }
        if (!file.getName().equals(newFileName)) {
            System.out.println(file.getName());
            System.out.println(newFileName);
            needChange = true;
        }
        StringBuilder result = new StringBuilder();
        if (replaceEnum == ReplaceTypeEnum.CONTENT || replaceEnum == ReplaceTypeEnum.ALL) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())))) {
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    String newLine = str.replaceAll(oldWord, newWord);
                    if (!str.equals(newLine)) {
                        System.out.println(str);
                        System.out.println(newLine);
                        needChange = true;
                    }
                    result.append(newLine).append('\n');
                }
            } catch (IOException e) {
                throw new BaseException("读取文件失败：" + file.getName());
            }
        }
        if (needChange) {
            String newLrcName = RegexUtil.last(file.getAbsolutePath(), "\\\\") + newFileName;
            if (replaceEnum == ReplaceTypeEnum.TITLE) {
                boolean changed = file.renameTo(new File(newLrcName));
                if (!changed) {
                    LOG.warn("{}修改文件名失败", file.getAbsolutePath());
                }
            } else {
                if (!file.delete()) {
                    throw new BaseException("删除文件失败：" + file.getName());
                }
                try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(
                        Paths.get(newLrcName))))) {
                    bufferedWriter.write(result.substring(0, result.length() - 1));
                    bufferedWriter.flush();
                } catch (IOException e) {
                    throw new BaseException("创建文件失败：" + file.getName());
                }
            }
            //lrc文件名变动，mp3文件名也跟着变
            if (!file.getName().equals(newFileName)) {
                File mp3File = new File(file.getAbsolutePath().replaceAll(FileTypeEnum.LRC.getSuffix(), FileTypeEnum.MP3.getSuffix()));
                boolean changed = mp3File.renameTo(new File(newLrcName.replaceAll(FileTypeEnum.LRC.getSuffix(), FileTypeEnum.MP3.getSuffix())));
                if (!changed) {
                    LOG.warn("{}联动修改对应的音频文件失败", file.getAbsolutePath());
                }
            }
        }
        return needChange ? 1 : 0;
    }

    public static int discardSymbol(File file) throws BaseException {
        //记录文件是否需要改动
        boolean needChange = false;
        //是否为奇数行
        boolean oddLine = true;
        StringBuilder result = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())))) {
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                if (oddLine) {
                    RegexUtil.checkLrcLine(str, file.getName());
                    //去除特殊符号
                    String newLine = formatOddLine(str);
                    if (!str.equals(newLine)) {
                        System.out.println(str);
                        System.out.println(newLine);
                        needChange = true;
                    }
                    String[] oddLineArr = newLine.split("]", 2);
                    if (StringUtils.isBlank(oddLineArr[1])) {
                        LOG.info("{}出现空字幕：{}", file.getAbsolutePath(), newLine);
                        bufferedReader.readLine();
                        needChange = true;
                        oddLine = false;
                    } else {
                        result.append(newLine).append('\n');
                    }
                } else {
                    RegexUtil.checkLrcSpaceLine(str, file.getName());
                    result.append(str).append('\n');
                }
                oddLine = !oddLine;
            }
        } catch (IOException e) {
            throw new BaseException("读取文件失败：" + file.getName());
        }
        if (needChange) {
            FileUtil.delete(file);
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(
                    Paths.get(file.getAbsolutePath()))))) {
                bufferedWriter.write(result.substring(0, result.length() - 1));
                bufferedWriter.flush();
            } catch (IOException e) {
                throw new BaseException("创建文件失败：" + file.getName());
            }
        }
        return needChange ? 1 : 0;
    }


    /**
     * 对奇数行去除特殊符号
     */
    private static String formatOddLine(String str) {
        String[] split = str.split("]", 2);
        String time = split[0] + "]";
        String word = split[1].trim();
        word = word.replaceAll("[\ufe0f　.。…,，·~、—－～〜♡❤♥❥♪!！?？⁉☆・\\s]+", StringUtils.SPACE);
        word = word.replaceAll("\\s+", StringUtils.SPACE);
        word = word.replaceAll("\\s+(?=[)）\\]】」』])", StringUtils.EMPTY);
        word = word.replaceAll("(?<=[(（\\[【「『])\\s+", StringUtils.EMPTY);
        word = word.replaceAll("^[.。!！,，?？;；]", StringUtils.EMPTY);
        word = word.replaceAll("[.。!！,，?？;；]$", StringUtils.EMPTY);
        word = word.replaceAll("\\s+", StringUtils.SPACE);
        return time + word.trim();
    }
}