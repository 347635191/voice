package 其他;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class 输出一个目录的系列 {
    private static String root = "E:\\AAA\\sound\\日语\\系列\\常规尺寸十到三十分钟作品";

    static{
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
    }

    public static void main(String[] args) {
        handle(new File(root));
    }

    public static void handle(File file) {
        for (File listFile : file.listFiles()) {
            if (listFile.isDirectory()) {
                for (File subFile : listFile.listFiles()) {
                    if (subFile.getName().endsWith(".mp3")) {
                        String xiLie = getXiLie(subFile);
                        System.out.println(listFile.getName() + "\t" + xiLie);
                        break;
                    }
                }
            }

        }
    }

    private static String getXiLie(File file) {
        MP3File mp3file;
        try {
            mp3file = new MP3File(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        AbstractID3v2Tag id3v2Tag = mp3file.getID3v2Tag();
        return id3v2Tag.getFirst(FieldKey.ALBUM_ARTIST);
    }
}