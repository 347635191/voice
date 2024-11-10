package component;

import com.yf.rj.common.FileConstants;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) throws Exception {
        File file = new File("E:\\AAA\\sound\\日语\\分类\\NTR\\RJ270553 长着大众脸的女朋友被寝取 从忍耐到堕入快乐\\1.对寝取者的抵抗.mp3");
        String str = file.getAbsolutePath();
        System.out.println(str);
        String oneCategory = "分类";
        String regex = "(?<=" + oneCategory + "\\\\).*?(?=\\\\RJ\\d+)";
        System.out.println(regex);
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}