package 中文;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class 逍遥统一设置艺术家 {
    private static final String FILE_PATH = "E:\\AAA\\sound\\国语\\逍遥(56部)";

    static {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
    }

    public static void main(String[] args) throws Exception {
        File file = new File(FILE_PATH);
        for (File listFile : file.listFiles()) {
            if (!listFile.getName().contains("--")) {
                System.out.println(listFile.getName());
                continue;
            }
            String artist = Arrays.stream(listFile.getName().split("--")[1].replaceAll(".mp3", "").split("、")).sorted().collect(Collectors.joining("/"));
            if (StringUtils.isNotBlank(artist)) {
                setAlbum(listFile, artist);
            } else {
                System.out.println(listFile.getName());
            }
        }
    }

    private static void setAlbum(File listFile, String name) throws Exception {
        MP3File mp3File = new MP3File(listFile);
        AbstractID3v2Tag id3v2Tag = mp3File.getID3v2Tag();
        id3v2Tag.setField(FieldKey.ARTIST, name);
        mp3File.save();
    }
}
