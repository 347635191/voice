package com.yf.rj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.regex.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum FileTypeEnum {
    MP3(0, ".*\\.mp3$") {
        @Override
        public boolean matches(File file) {
            return file.isFile() && Pattern.compile(getRegex()).matcher(file.getName()).matches();
        }
    },
    LRC(1, ".*\\.lrc$") {
        @Override
        public boolean matches(File file) {
            return file.isFile() && Pattern.compile(getRegex()).matcher(file.getName()).matches();
        }
    },
    DIR(2, null) {
        @Override
        public boolean matches(File file) {
            return file.isDirectory();
        }
    };

    private Integer code;
    private String regex;

    public abstract boolean matches(File file);
}