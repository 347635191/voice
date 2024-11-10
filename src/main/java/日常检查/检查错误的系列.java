package 日常检查;

import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import 实体类.统一文件接口;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  检查错误的系列 implements 统一文件接口 {
    //系列文件夹名成，系列名称
    public static final Map<String, String> map = new HashMap<>();

    //判断同一系列是否有重复创建文件夹的
    public static final List<String> list = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new 检查错误的系列().process(new File("E:\\AAA\\sound\\日语\\系列"));
        map.forEach((key, value) -> {
            if (!list.contains(value)) {
                list.add(value);
            } else {
                System.out.println(key + "\t" + value + "系列重复");
            }
        });
    }

    @Override
    public void handle(File file) throws Exception {
        String absolutePath = file.getAbsolutePath();
        absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf('\\'));
        for (File listFile : file.listFiles()) {
            if (listFile.getName().endsWith(".mp3")) {
                String xiLieGet = getXiLie(listFile).trim();
                if (StringUtils.isBlank(xiLieGet)) {
                    System.out.println(file.getAbsolutePath() + "没有系列");
                    break;
                }
                String xiLie = map.get(absolutePath);
                if (xiLie == null) {
                    map.put(absolutePath, xiLieGet);
                } else {
                    if (!StringUtils.equals(xiLie, xiLieGet)) {
                        System.out.println(xiLie + "\t" + xiLieGet);
                        System.out.println(file.getAbsolutePath() + "系列不正确");
                    }
                    break;
                }
            }
        }
    }

    private String getXiLie(File file) throws Exception {
        MP3File mp3file = new MP3File(file);
        AbstractID3v2Tag id3v2Tag = mp3file.getID3v2Tag();
        return id3v2Tag.getFirst(FieldKey.ALBUM_ARTIST);
    }
}