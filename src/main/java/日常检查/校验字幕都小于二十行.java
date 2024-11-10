package 日常检查;

import cn.hutool.core.io.FileUtil;
import 实体类.统一文件接口;

import java.io.File;

/**
 * 忘记加H
 */
public class 校验字幕都小于二十行 implements 统一文件接口 {
    private static final int MIN_LINE = 20;

    public static void main(String[] args) throws Exception {
        new 校验字幕都小于二十行().process();
    }

    public void handle(File file) {
        boolean AllLowerFlag = true;
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File file1 : files) {
            if (file1.getName().endsWith(".lrc") && !LowerTenLine(file1)) {
                AllLowerFlag = false;
                break;
            }
        }
        if (AllLowerFlag) {
            System.out.println(file.getAbsolutePath());
        }
    }

    private static boolean LowerTenLine(File file) {
        int totalLines = FileUtil.getTotalLines(file);
        return totalLines < MIN_LINE;
    }
}
