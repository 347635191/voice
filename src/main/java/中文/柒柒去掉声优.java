package 中文;

import java.io.File;

public class 柒柒去掉声优 {
    private static final String FILE_PATH = "E:\\AAA\\sound\\国语\\柒柒(原糖果屋)";

    public static void main(String[] args) throws Exception {
        File file = new File(FILE_PATH);
        for (File listFile : file.listFiles()) {
            listFile.renameTo(new File("E:\\AAA\\sound\\国语\\柒柒(原糖果屋)\\" + listFile.getName().split("--")[0] + ".mp3"));
        }
    }
}