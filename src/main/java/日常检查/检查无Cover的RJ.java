package 日常检查;

import org.apache.commons.lang3.StringUtils;
import 实体类.统一文件接口;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class 检查无Cover的RJ implements 统一文件接口 {
    //有PV所以没有封面的RJ
    private static List<String> list = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new 检查无Cover的RJ().process();
    }

    public void handle(File file) {
        for (String s : list) {
            if (file.getName().contains(s)) {
                return;
            }
        }
        boolean flag = true;
        for (File listFile : file.listFiles()) {
            String name = listFile.getName();
            if (StringUtils.equalsAny(name, "cover.jpg", "cover.png")) {
                flag = false;
            }
        }
        if (flag) {
            System.out.println(file.getAbsolutePath());
        }
    }

    static {
        list.add("RJ01107806");
        list.add("RJ01122687");
        list.add("RJ01194646");
        list.add("RJ01210551");
        list.add("RJ377799");
        list.add("RJ01036859");
        list.add("RJ01044949");
        list.add("RJ01119777");
        list.add("RJ01125827");
        list.add("RJ01152082");
        list.add("RJ436013");
        list.add("RJ01112591");
        list.add("RJ01122276");
        list.add("RJ01187504");
        list.add("RJ01034675");
        list.add("RJ01026994");
        list.add("RJ01215849");
        list.add("RJ01043708");
        list.add("RJ434523");
        list.add("RJ315947");
        list.add("RJ01105046");
        list.add("RJ01123010");
        list.add("RJ435170");
        list.add("RJ01129894");
        list.add("RJ01154221");
        list.add("RJ01182133");
        list.add("RJ01165940");
        list.add("RJ01145721");
        list.add("RJ01101042");
        list.add("RJ01106592");
        list.add("RJ327269");
        list.add("RJ01062146");
        list.add("RJ338717");
        list.add("RJ01212146");
        list.add("RJ435993");
        list.add("RJ01063263");
        list.add("RJ01074683");
        list.add("RJ01145742");
        list.add("RJ01173697");
        list.add("RJ01085834");
        list.add("RJ01029010");
        list.add("RJ410177");
        list.add("RJ01166169");
        list.add("RJ01215544");
        list.add("RJ302768");
        list.add("RJ437905");
        list.add("RJ01169989");
        list.add("RJ01045151");
        list.add("RJ01051896");
        list.add("RJ01123509");
        list.add("RJ01129908");
        list.add("RJ01066239");
        list.add("RJ01131014");
        list.add("RJ01131775");
        list.add("RJ01053422");
        list.add("RJ01112220");
        list.add("RJ01159587");
        list.add("RJ01178269");
        list.add("RJ01013472");
        list.add("RJ01095812");
        list.add("RJ01181752");
        list.add("RJ01191730");
        list.add("RJ01013278");
        list.add("RJ01026971");
        list.add("RJ01045981");
        list.add("RJ01058313");
        list.add("RJ431190");
        list.add("RJ01095149");
        list.add("RJ362056");
        list.add("RJ01072791");
        list.add("RJ01103771");
        list.add("RJ01113187");
        list.add("RJ01122915");
        list.add("RJ01134131");
        list.add("RJ01149793");
        list.add("RJ01165316");
        list.add("RJ01178141");
        list.add("RJ01192535");
        list.add("RJ01203798");
        list.add("RJ01143998");
        list.add("RJ01024807");
        list.add("RJ01024414");
        list.add("RJ01051839");
        list.add("RJ01135937");
        list.add("RJ01193818");
        list.add("RJ01144979");
        list.add("RJ01095692");
        list.add("RJ01140415");
        list.add("RJ01077518");
        list.add("RJ01126387");
        list.add("RJ01212566");
        list.add("RJ01110454");
        list.add("RJ01196540");
        list.add("RJ01187636");
        list.add("RJ01034762");
        list.add("RJ01060773");
        list.add("RJ01073051");
        list.add("RJ01133015");
    }
}
