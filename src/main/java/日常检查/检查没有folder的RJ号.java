package 日常检查;

import 实体类.统一文件接口;

import java.io.File;

public class 检查没有folder的RJ号 implements 统一文件接口 {
    public static void main(String[] args) throws Exception {
        new 检查没有folder的RJ号().process();
    }

    public void handle(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.getName().equals("folder.jpg")) {
                    return;
                }
            }
        }
        System.out.println(file.getAbsolutePath());
    }
}
