package ui.components.input;

import javax.swing.*;
import java.awt.*;

public abstract class InputPanel extends JPanel {
    private JLabel label;

    public InputPanel(String labelText) {
        this(labelText, true);
    }

    public InputPanel(String labelText, boolean rightLabel) {
        label = new JLabel(labelText);
        setOpaque(false);
        initializeComponents();
        if (rightLabel) {
            addAllComponents(label);
            addComponents();
        } else {
            addComponents();
            addAllComponents(label);
        }
        setMaximumSize(getPreferredSize());
    }

    // MODIFIES: this
    // EFFECTS: initialize the components of the panel
    public abstract void initializeComponents();

    // MODIFIES: this
    // EFFECTS: add components to this
    public abstract void addComponents();

    // MODIFIES: this
    // EFFECTS: adds components to this
    public void addAllComponents(Component... components) {
        for (Component c : components) {
            add(c);
        }
    }
}
