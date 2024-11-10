package 日常检查;

import 实体类.FileConst;

import java.io.*;

public class 检查优化为图片文件夹 {
    public static void main(String[] args) throws Exception {
        process();
    }

    private static void LoopHandleAllDir(File root) throws Exception {
        if (root.isDirectory()) {
            handle(root);
            File[] files = root.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    LoopHandleAllDir(file);
                }
            }
        }
    }

    private static void process() throws Exception {
        LoopHandleAllDir(new File(FileConst.ROOT_PATH));
    }

    public static void handle(File file) throws IOException {
        boolean flag = true;
        for (File file1 : file.listFiles()) {
            if (file1.getName().equals("desktop.ini")) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1.getAbsolutePath())));
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    if (str.contains("FolderType=Pictures")) {
                        flag = false;
                        break;
                    }
                }
                bufferedReader.close();
            }
        }
        if (flag) {
            System.out.println(file.getAbsolutePath());
        }
    }
}
