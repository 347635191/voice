package 统一处理;

import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import 实体类.统一文件接口;

import java.io.File;

public class 统一设置专辑 implements 统一文件接口 {
    public static void main(String[] args) throws Exception {
        new 统一设置专辑().process();
    }

    @Override
    public void handle(File file) throws Exception {
        for (File listFile : file.listFiles()) {
            if (listFile.getName().endsWith(".mp3")) {
                setAlbum(listFile, file.getName());
            }
        }
    }

    private void setAlbum(File listFile, String name) throws Exception {
        MP3File mp3File = new MP3File(listFile);
        AbstractID3v2Tag id3v2Tag = mp3File.getID3v2Tag();
        id3v2Tag.setField(FieldKey.ALBUM, name);
        mp3File.save();
        System.out.println(name);
    }
}
