package 日常检查;

import component.ThreadPoolUtils;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import 实体类.统一文件接口;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class 检查音频比特率 implements 统一文件接口 {
    private static final String MAX_BIT_RATE = "320";

    public static void main(String[] args) throws Exception {
        new 检查音频比特率().process();
        ThreadPoolUtils.shutdown();
    }

    public void handle(File file) throws Exception {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        boolean flag = false;
        for (File file1 : file.listFiles()) {
            if (file1.getName().endsWith(".mp3")) {
                String bitRate;
                if (!(bitRate = getBitRate(file1.getAbsolutePath())).equals(MAX_BIT_RATE)) {
                    System.out.println(file1.getName() + ' ' + bitRate);
                    flag = true;
                }
            }
        }
        if (flag) {
            System.out.println((file.getAbsolutePath()));
        }
    }

    private static String getBitRate(String mp3Path) {
        MP3File mp3File = null;
        try {
            mp3File = new MP3File(mp3Path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        MP3AudioHeader header = mp3File.getMP3AudioHeader();
        return header.getBitRate();
    }
}