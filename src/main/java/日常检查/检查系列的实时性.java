package 日常检查;

import component.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class 检查系列的实时性 {
    private static String fenLeiPath = "E:\\AAA\\sound\\日语\\分类";
    private static String xiLiePath = "E:\\AAA\\sound\\日语\\系列";
    private static StringBuilder error = new StringBuilder();
    private static String out = "C:\\Users\\lufeii\\Desktop\\out";

    public static void main(String[] args) {
        File[] fenLeiListFile = new File(fenLeiPath).listFiles();
        collect(fenLeiListFile);
        File[] xiLieListFile = new File(xiLiePath).listFiles();
        collect(xiLieListFile);

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(out + "\\\\" + "DLsiteXiLie.txt"))));
            bufferedWriter.write(error.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void collect(File[] listFile) {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
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
                File firstSubFile = null;
                for (File subFile : file.listFiles()) {
                    if (subFile.getName().endsWith(".mp3")) {
                        firstSubFile = subFile;
                        break;
                    }
                }
                String curXiLie = getXiLie(firstSubFile);
                String newXiLie = HttpClientUtils.getXiLie(rj);
                if(StringUtils.isNotBlank(newXiLie)){
                    System.out.println(newXiLie);
                }
                if ("-1".equals(newXiLie)) {
                    error.append(rj + "\t" + curXiLie + "\t" + "未找到新系列" + "\n");
                    continue;
                }
                if (!StringUtils.equals(curXiLie, newXiLie)) {
                    error.append(rj + "\t" + curXiLie + "\t" + newXiLie + "\n");
                }
            }
        }
    }

    private static String getXiLie(File file) {
        MP3File mp3file = null;
        try {
            mp3file = new MP3File(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AbstractID3v2Tag id3v2Tag = mp3file.getID3v2Tag();
        return id3v2Tag.getFirst(FieldKey.ALBUM_ARTIST);
    }
}