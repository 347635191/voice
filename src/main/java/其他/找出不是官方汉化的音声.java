package 其他;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import 实体类.统一文件接口;

import java.io.File;

public class 找出不是官方汉化的音声 implements 统一文件接口 {
    public static void main(String[] args) throws Exception {
        new 找出不是官方汉化的音声().process();
    }

    public void handle(File file) throws Exception {
        for (File listFile : file.listFiles()) {
            if (listFile.getName().endsWith(".mp3")) {
                String genre = getGenre(listFile);
                if (!StringUtils.equalsAny(genre, "官方汉化", "橙澄子汉化", "风花雪月汉化", "北极星汉化")) {
                    System.out.println(file.getAbsolutePath());
//                    System.out.println(file.getName().split(" ")[0] + '\t' + genre);
                    break;
                }
            }
        }
    }

    private static String getGenre(File file) throws Exception {
        MP3File mp3file = new MP3File(file);
        AbstractID3v2Tag id3v2Tag = mp3file.getID3v2Tag();
        return id3v2Tag.getFirst(FieldKey.GENRE);
    }
}
