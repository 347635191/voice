package 日常检查;

import org.apache.commons.lang3.StringUtils;
import 实体类.统一文件接口;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class 检查文件和图片序号正确性 implements 统一文件接口 {
    public static void main(String[] args) throws Exception {
        new 检查文件和图片序号正确性().process();
    }

    public void handle(File file) {
        //LRC和MP3序号
        List<Integer> fileNameList = Arrays.stream(file.listFiles()).map(File::getName).filter(name -> name.endsWith(".lrc") || name.endsWith(".mp3")).map(name -> {
            name = name.split("\\.")[0];
            try {
                return Integer.parseInt(name);
            }catch(NumberFormatException e){
                System.err.println(file.getAbsolutePath());
                throw e;
            }
        }).sorted().collect(Collectors.toList());
        for (int i = 1; i <= fileNameList.size(); i++) {
            int seq = (int) Math.ceil((double) i / 2);
            if (fileNameList.get(i - 1) != seq) {
                System.out.println("音频");
                System.out.println(file.getAbsolutePath());
                break;
            }
        }

        //检查图片序号
        List<Integer> picNameList = Arrays.stream(file.listFiles()).map(File::getName).filter(name -> name.endsWith(".jpg") || name.endsWith(".png")).filter(name -> !StringUtils.equalsAny(name, "folder.jpg","cover.jpg","cover.png")).map(name -> {
            name = name.split("\\.")[0].replace("main", "");
            if(name.isEmpty()){
                name = "0";
            }
            try {
                return Integer.parseInt(name);
            }catch(NumberFormatException e){
                System.err.println(file.getAbsolutePath());
                throw e;
            }
        }).sorted().collect(Collectors.toList());
        for (int j = 0; j < picNameList.size(); j++) {
            if (j != picNameList.get(j)) {
                System.out.println("图片");
                System.out.println(file.getAbsolutePath());
                break;
            }
        }
    }
}
