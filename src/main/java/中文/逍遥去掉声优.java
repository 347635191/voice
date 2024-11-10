package 中文;

import java.io.File;

public class 逍遥去掉声优 {
    private static final String FILE_PATH = "E:\\AAA\\sound\\国语\\逍遥(56部)";

    public static void main(String[] args) throws Exception {
        File file = new File(FILE_PATH);
        for (File listFile : file.listFiles()) {
            listFile.renameTo(new File("E:\\AAA\\sound\\国语\\逍遥(56部)\\" + listFile.getName().split("--")[0] + ".mp3"));
        }
    }
}
