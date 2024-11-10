package 日常检查;

import 实体类.统一文件接口;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class 校验相同RJ号 implements 统一文件接口 {
    private static List<String> nameList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new 校验相同RJ号().process();
        System.out.println(nameList.size());
    }

    public void handle(File file) {
        String name = file.getName().split(" ")[0];
        if (nameList.contains(name)) {
            System.out.println(file.getAbsolutePath());
        } else {
            nameList.add(name);
        }
    }
}
