package 其他;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class 找出橙橙子汉化未收集的 {
    private static String path1 = "C:\\Users\\lufeii\\Desktop\\aaa.txt";
    private static String fenLeiPath = "E:\\AAA\\sound\\日语\\分类";
    private static String xiLiePath = "E:\\AAA\\sound\\日语\\系列";
    private static List<String> rjList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        File[] fenLeiListFile = new File(fenLeiPath).listFiles();
        collect(fenLeiListFile);
        File[] xiLieListFile = new File(xiLiePath).listFiles();
        collect(xiLieListFile);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path1))));
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            String rj = str.trim();
            if (!rjList.contains(rj.trim())) {
                System.out.println(rj);
            }
        }
        bufferedReader.close();
    }

    private static void collect(File[] listFile) {
        for (File outerFile : listFile) {
            if (outerFile.getName().endsWith("ini")) {
                continue;
            }
            File[] files = outerFile.listFiles();
            for (File file : files) {
                if (file.getName().endsWith("ini")) {
                    continue;
                }
                String rj = file.getName().split(" ")[0];
                rjList.add(rj);
            }
        }
    }
}
