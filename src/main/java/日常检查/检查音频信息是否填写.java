package 日常检查;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import 实体类.统一文件接口;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class 检查音频信息是否填写 implements 统一文件接口 {
    public static void main(String[] args) throws Exception {
        new 检查音频信息是否填写().process();
    }

    public void handle(File file) throws Exception {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        Map<String, String> map = new HashMap<>();
        for (File file1 : file.listFiles()) {
            if (file1.getName().endsWith(".mp3")) {
                if (validInfo(file1.getAbsolutePath(), map)) {
                    System.out.println(file1.getAbsolutePath());
                }
            }
        }
        map.clear();
        map = null;
    }

    private static boolean validInfo(String filePath, Map<String, String> map) {
        MP3File mp3file = null;
        try {
            mp3file = new MP3File(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        AbstractID3v2Tag id3v2Tag = mp3file.getID3v2Tag();
        String title = id3v2Tag.getFirst(FieldKey.TITLE);
        boolean flag = false;
        if (checkEle(title)) {
            System.out.println(title);
            flag = true;
        }
        String artist = id3v2Tag.getFirst(FieldKey.ARTIST);
        if (checkEle(artist) || checkArtist(artist) || checkSame(map, "声优", artist)) {
            System.out.println(artist);
            flag = true;
        }
        String album = id3v2Tag.getFirst(FieldKey.ALBUM);
        if (checkEle(album) || checkSame(map, "唱片集", album)) {
            System.out.println(album);
            flag = true;
        }
        String year = id3v2Tag.getFirst(FieldKey.YEAR);
        if (checkEle(year) || checkSame(map, "年", year)) {
            System.out.println(year);
            flag = true;
        }
        String comment = id3v2Tag.getFirst(FieldKey.COMMENT);
        if (checkEle(comment) || comment.contains(" ") || checkSame(map, "注释", comment)) {
            System.out.println(comment);
            flag = true;
        }
        String genre = id3v2Tag.getFirst(FieldKey.GENRE); //官方汉化
        if (checkEle(genre) || checkSame(map, "流派", genre)) {
            System.out.println(genre);
            flag = true;
        }
        if (!StringUtils.equalsAny(genre, "官方汉化", "风花雪月汉化", "橙澄子汉化", "北极星汉化")) {
            System.out.println(genre);
            flag = true;
        }
        String xiLie = id3v2Tag.getFirst(FieldKey.ALBUM_ARTIST);
        if (xiLie.trim().length() != xiLie.length() || checkSame(map, "系列", xiLie)) {
            System.out.println(xiLie);
            flag = true;
        }
        String composer = id3v2Tag.getFirst(FieldKey.COMPOSER);
        if (checkEle(composer) || checkSame(map, "社团", composer)) {
            System.out.println(composer);
            flag = true;
        }
        return flag;
    }

    private static boolean checkEle(String ele) {
        return StringUtils.isBlank(ele) || ele.trim().length() != ele.length();
    }

    private static boolean checkArtist(String artist) {
        if (StringUtils.containsAny(artist, "もときりお", "紅月ことね,乙倉由依", "白花このみ")) {
            return true;
        }
        List<String> checkList = Arrays.asList("そらまめ", "MOMOKA", "みぃ～な", "神崎ゆら");
        for (String s : checkList) {
            if (artist.contains(s) && !artist.contains(s + "。")) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkSame(Map<String, String> map, String key, String value) {
        if (map.containsKey(key)) {
            if (!StringUtils.equals(map.get(key), value)) {
                return true;
            }
        } else {
            map.put(key, value);
        }
        return false;
    }
}
