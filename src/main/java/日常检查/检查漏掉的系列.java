package 日常检查;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import 实体类.统一文件接口;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class 检查漏掉的系列 implements 统一文件接口 {
    //系列文件夹外set
    private static Set<String> set1 = new HashSet<>();
    //系列文件夹内set
    private static Set<String> set2 = new HashSet<>();

    public static void main(String[] args) throws Exception {
        new 检查漏掉的系列().process();
        for (String next : set1) {
            if (set2.contains(next)) {
                System.out.println(next);
            }
        }
    }

    @Override
    public void handle(File file) throws Exception {
        String absolutePath = file.getAbsolutePath();
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf('\\'));
        for (File listFile : file.listFiles()) {
            if (listFile.getName().endsWith(".mp3")) {
                String xiLie = getXiLie(listFile).trim();
                if (StringUtils.isNotBlank(xiLie)) {
                    if (absolutePath.contains("日语\\系列")) {
                        set2.add(xiLie);
                    } else {
                        set1.add(xiLie);
                    }
                    break;
                }
            }
        }
    }

    private String getXiLie(File file) throws Exception {
        MP3File mp3file = new MP3File(file);
        AbstractID3v2Tag id3v2Tag = mp3file.getID3v2Tag();
        return id3v2Tag.getFirst(FieldKey.ALBUM_ARTIST);
    }
}
