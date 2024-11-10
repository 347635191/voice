package component;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardUtils {
    private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static String read() {
        Transferable content = clipboard.getContents(null);
        if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String text;
            try {
                text = (String) content.getTransferData(DataFlavor.stringFlavor);
                text = text.replaceAll("\n", "").replaceAll("\t", "").trim();
                return text;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void write(String word) {
        StringSelection selection = new StringSelection(word);
        clipboard.setContents(selection, null);
    }
}
