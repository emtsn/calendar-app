package ui.components.display;

import ui.VisualEditor;

import javax.swing.*;
import java.awt.*;

public class TitleLabel extends JLabel {
    public TitleLabel(String text) {
        this(text, true);
    }

    public TitleLabel(String text, boolean isMain) {
        this(text, isMain, false, true);
    }

    public TitleLabel(String text, boolean isMain, boolean top, boolean bottom) {
        this(text, isMain, top ? 1 : 0, bottom ? 1 : 0);
    }

    public TitleLabel(String text, boolean isMain, int top, int bottom) {
        super(text, SwingConstants.CENTER);
        setBackground(isMain ? VisualEditor.TITLE_LABEL_COLOR : VisualEditor.SUBTITLE_LABEL_COLOR);
        setForeground(VisualEditor.TITLE_LABEL_TEXT_COLOR);
        setBorder(BorderFactory.createMatteBorder(top, 0, bottom, 0, VisualEditor.TITLE_LABEL_BORDER_COLOR));
        setOpaque(true);
        setMaximumSize(new Dimension(VisualEditor.WIDTH, getPreferredSize().height));
    }
}
