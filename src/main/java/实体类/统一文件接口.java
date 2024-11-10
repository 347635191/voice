package 实体类;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface 统一文件接口 {
    void handle(File file) throws Exception;

    default void LoopHandleAllDir(File root) throws Exception {
        if (root.isDirectory()) {
            if (root.getName().contains("RJ")) {
                handle(root);
            } else {
                File[] files = root.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        LoopHandleAllDir(file);
                    }
                }
            }
        }
    }

    default void process() throws Exception {
        //关闭jaudiotagger日志打印
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        LoopHandleAllDir(new File(FileConst.ROOT_PATH));
    }

    default void process(File root) throws Exception {
        //关闭jaudiotagger日志打印
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        LoopHandleAllDir(root);
    }
}
