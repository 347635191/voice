package com.yf.rj.util;

import com.yf.rj.common.FileConstants;
import com.yf.rj.entity.Mp3T;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

    public static Integer getBitRate(File file) {
        MP3File mp3File;
        try {
            mp3File = new MP3File(file);
            MP3AudioHeader header = mp3File.getMP3AudioHeader();
            return Integer.parseInt(header.getBitRate());
        } catch (Exception e) {
            log.error("获取比特率失败，文件名：{}", file.getName());
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
            log.error("获取时常失败，文件名：{}", file.getName());
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
            log.error("获取{}失败，文件名：{}", key.getClass().getName(), file.getName());
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
            mp3T.setTitle(id3v2Tag.getFirst(FieldKey.TITLE));
            //唱片集
            mp3T.setAlbum(id3v2Tag.getFirst(FieldKey.ALBUM));
            //声优
            mp3T.setArtist(id3v2Tag.getFirst(FieldKey.ARTIST));
            //年份
            mp3T.setYear(id3v2Tag.getFirst(FieldKey.YEAR));
            //汉化组名称
            mp3T.setGenre(id3v2Tag.getFirst(FieldKey.GENRE));
            //标签
            mp3T.setComment(id3v2Tag.getFirst(FieldKey.COMMENT));
            //系列
            mp3T.setSeries(id3v2Tag.getFirst(FieldKey.ALBUM_ARTIST));
            //社团
            mp3T.setComposer(id3v2Tag.getFirst(FieldKey.COMPOSER));
        } catch (Exception e) {
            log.error("获取属性失败，文件名：{}", file.getName());
        }
    }

    public static String getRj(File file) {
        String regex = "RJ\\d+";
        Matcher matcher = Pattern.compile(regex).matcher(file.getAbsolutePath());
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static Integer getSeq(File file) {
        String regex = "^\\d+";
        Matcher matcher = Pattern.compile(regex).matcher(file.getName());
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return null;
    }

    public static String getRjName(File file) {
        String regex = "RJ\\d+.*?(?=\\\\\\d+\\.)";
        Matcher matcher = Pattern.compile(regex).matcher(file.getAbsolutePath());
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getOneCategory(File file) {
        String regex = "(?<=" + FileConstants.THIRD_DIR_NAME + "\\\\).*?(?=\\\\)";
        Matcher matcher = Pattern.compile(regex).matcher(file.getAbsolutePath());
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getTwoCategory(File file) {
        String regex = "[^\\\\]+$";
        Matcher matcher = Pattern.compile(regex).matcher(file.getAbsolutePath());
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getTwoCategory(File file, String oneCategory) {
        String regex = "(?<=" + oneCategory + "\\\\).*?(?=\\\\RJ\\d+)";
        Matcher matcher = Pattern.compile(regex).matcher(file.getAbsolutePath());
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getCreateTime(File file) {
        BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(Paths.get(file.getAbsolutePath()), BasicFileAttributes.class);
            FileTime fileTime = attributes.creationTime();
            return DateUtil.formatDateTime(fileTime);
        } catch (IOException e) {
            log.error("获取文件创建时间失败，文件名：{}", file.getName());
            return null;
        }
    }
}