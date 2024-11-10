package 日常检查;

import org.apache.commons.collections4.CollectionUtils;
import 实体类.FileConst;
import 实体类.统一文件接口;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class 校验封面和RJ号对应 implements 统一文件接口 {
    private static List<String> list1 = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new 校验封面和RJ号对应().process();

        List<String> list2 = new ArrayList<>();
        File[] files2 = new File(FileConst.COVER_PATH).listFiles();
        for (File file : files2) {
            if (file.getName().equals("封面") || file.getName().equals("desktop.ini")) {
                continue;
            }
            if(!Pattern.matches("RJ\\d+.jpg", file.getName())){
                System.out.println(file.getName() + "封面格式不对");
            }
            list2.add(file.getName().split("\\.")[0]);
        }
        System.out.println(list1.size() == list2.size());
        Collection<String> subtract1 = CollectionUtils.subtract(list1, list2);
        Collection<String> subtract2 = CollectionUtils.subtract(list2, list1);
        System.out.println(subtract1);
        System.out.println(subtract2);
    }

    public void handle(File file) {
        list1.add(file.getName().split(" ")[0]);
    }
}
