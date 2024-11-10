package 日常检查;


import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import 实体类.统一文件接口;

import java.io.File;

public class 检查唱片集是否和文件夹名称一致 implements 统一文件接口 {

    public static void main(String[] args) throws Exception {
        new 检查唱片集是否和文件夹名称一致().process();
    }

    public void handle(File file) throws Exception {
        for (File listFile : file.listFiles()) {
            if (listFile.getName().endsWith(".mp3")) {
                if (!validChangPianJi(listFile, file.getName())) {
                    System.out.println(listFile.getName());
                    System.out.println(file.getAbsolutePath());
                    break;
                }
            }
        }
    }

    private static boolean validChangPianJi(File file, String dirName) throws Exception {
        MP3File mp3File = new MP3File(file);
        AbstractID3v2Tag id3v2Tag = mp3File.getID3v2Tag();
        return dirName.equals(id3v2Tag.getFirst(FieldKey.ALBUM));
    }
}
