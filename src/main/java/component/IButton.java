package component;

import javax.swing.*;
import java.awt.*;

public class IButton extends JButton {
    public IButton(String text, int x, int y, int w, int h) {
        super(text);
        setBounds(x, y, w, h);
        setFont(new Font("黑体", Font.BOLD, 16));
        setBackground(new Color(255, 255, 255));
        setForeground(new Color(51, 51, 51));
        setFocusPainted(false);
    }
}
