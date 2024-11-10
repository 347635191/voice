package component;

import javax.swing.*;
import java.awt.*;

public class ITextArea extends JTextArea {
    public ITextArea(int x, int y, int w, int h) {
        setBounds(x, y, w, h);
        setFont(new Font("黑体", Font.BOLD, 13));
        setLineWrap(true);

//        setBorder(BorderFactory.createLineBorder(new Color(196, 199, 206), 2));
//        addFocusListener(new FocusListener() {
//            @Override
//            public void focusGained(FocusEvent e) {
//                setBorder(BorderFactory.createLineBorder(new Color(78, 113, 242), 2));
//            }
//
//            @Override
//            public void focusLost(FocusEvent e) {
//                // TODO  点击窗口，不失去焦点
//                setBorder(BorderFactory.createLineBorder(new Color(196, 199, 206), 2));
//            }
//        });
    }
}