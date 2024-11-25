package com.yf.rj.util;

import com.yf.rj.dto.BaseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClipboardUtil {
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static String read() throws BaseException {
        Transferable content = clipboard.getContents(null);
        if (!content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            throw new BaseException("剪切板里不是文字类型");
        }
        try {
            String word = (String) content.getTransferData(DataFlavor.stringFlavor);
            word = word.trim();
            if(StringUtils.isBlank(word)){
                throw new BaseException("剪切板内容为空");
            }
            return word;
        } catch (Exception e) {
            throw new BaseException("读取剪切板失败");
        }
    }

    public static void write(String word) {
        StringSelection selection = new StringSelection(word);
        clipboard.setContents(selection, null);
    }
}