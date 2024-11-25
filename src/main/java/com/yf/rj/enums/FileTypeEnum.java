package com.yf.rj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum FileTypeEnum {
    DIR(0, null, null) {
        @Override
        public boolean match(File file) {
            return file.isDirectory();
        }
    },
    MP3(1, ".*\\.mp3$", ".mp3"),
    LRC(2, ".*\\.lrc$", ".lrc"),
    VTT(3, ".*\\.vtt$", ".vtt"),
    TXT(4, ".*\\.txt$", ".txt"),
    SRT(5, ".*\\.srt$", ".srt"),
    WAV(6, ".*\\.wav$", ".wav"),
    FLAC(7, ".*\\.flac$", ".flac"),
    JPG(8, ".*\\.jpg$", ".jpg"),
    INI(9, ".*\\.ini$", ".ini");

    private Integer code;
    private String regex;
    private String suffix;
    private static final List<FileTypeEnum> values = Arrays.asList(values());

    public boolean match(File file) {
        return file.isFile() && Pattern.compile(getRegex()).matcher(file.getName()).matches();
    }

    public boolean match(String fileName) {
        return Pattern.compile(getRegex()).matcher(fileName).matches();
    }

    public boolean notMatch(String fileName){
        return !match(fileName);
    }

    public static List<String> getSuffixes() {
        return values.stream().map(FileTypeEnum::getSuffix)
                .filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }
}