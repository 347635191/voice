package 中文;

import java.io.File;

public class 阿木木去掉声优 {
    private static final String FILE_PATH = "E:\\AAA\\sound\\国语\\阿木木(2024元旦)";

    public static void main(String[] args) throws Exception {
        File file = new File(FILE_PATH);
        for (File listFile : file.listFiles()) {
            listFile.renameTo(new File("E:\\AAA\\sound\\国语\\阿木木(2024元旦)\\" + listFile.getName().split("--")[0] + ".mp3"));
        }
    }
}
