package 统一处理;

import 实体类.统一文件接口;

import java.io.File;

public class 统一改标签 implements 统一文件接口 {
    private static final String aTag = "【玩胸部】";
    private static final String bTag = "";

    public static void main(String[] args) throws Exception {
        new 统一改标签().process();
    }

    @Override
    public void handle(File file) throws Exception {
        for (File listFile : file.listFiles()) {
            String name = listFile.getName();
            if(name.contains(aTag)){
                System.out.println(listFile.getAbsolutePath());
                listFile.renameTo(new File(listFile.getAbsolutePath().replace(aTag, bTag)));
            }
        }
    }
}
